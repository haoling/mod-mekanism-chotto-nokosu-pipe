package com.haoling.chottonokosu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * Pulls from the inventory behind it and pushes into the block in front (a chest, a Mekanism Logistical Transporter, ...).
 * For each item type it only moves what exceeds {@code keep}: total - keep, at most {@code batch} per operation.
 */
public class ChottoNokosuPipeBlockEntity extends BlockEntity implements MenuProvider {
    public static final int MAX_KEEP = 30000;
    public static final int INTERVAL = 8;

    /** null = any item. */
    private Item filter = null;
    private int keep = 64;
    private int batch = 16;

    public ChottoNokosuPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ChottoNokosuPipeMod.PIPE_BE.get(), pos, state);
    }

    public Item getFilter() { return filter; }
    public int getKeep() { return keep; }
    public int getBatch() { return batch; }

    public void setFilter(Item item) { this.filter = item; setChanged(); }
    public void setKeep(int v) { this.keep = Math.max(0, Math.min(MAX_KEEP, v)); setChanged(); }
    public void setBatch(int v) { this.batch = Math.max(1, Math.min(64, v)); setChanged(); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChottoNokosuPipeBlockEntity be) {
        if (level.getGameTime() % INTERVAL != 0) return;
        Direction out = state.getValue(ChottoNokosuPipeBlock.FACING);
        IItemHandler src = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(out.getOpposite()), out);
        IItemHandler dst = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(out), out.getOpposite());
        if (src == null || dst == null) return;
        be.transfer(src, dst);
    }

    private void transfer(IItemHandler src, IItemHandler dst) {
        for (int slot = 0; slot < src.getSlots(); slot++) {
            ItemStack inSlot = src.getStackInSlot(slot);
            if (inSlot.isEmpty()) continue;
            if (filter != null && !inSlot.is(filter)) continue;

            int total = 0;
            for (int i = 0; i < src.getSlots(); i++) {
                ItemStack s = src.getStackInSlot(i);
                if (ItemStack.isSameItemSameComponents(s, inSlot)) total += s.getCount();
            }
            int surplus = total - keep;
            if (surplus <= 0) continue;

            int want = Math.min(Math.min(surplus, batch), inSlot.getCount());
            ItemStack simulated = src.extractItem(slot, want, true);
            if (simulated.isEmpty()) continue;
            ItemStack leftover = ItemHandlerHelper.insertItem(dst, simulated, true);
            int movable = simulated.getCount() - leftover.getCount();
            if (movable <= 0) continue;

            ItemStack taken = src.extractItem(slot, movable, false);
            if (taken.isEmpty()) continue;
            ItemStack rest = ItemHandlerHelper.insertItem(dst, taken, false);
            if (!rest.isEmpty()) {
                // Should not happen after the simulation; hand it back rather than lose it.
                rest = ItemHandlerHelper.insertItem(src, rest, false);
                if (!rest.isEmpty() && level != null) {
                    net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, rest);
                }
            }
            return; // one move per cycle
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (filter != null) tag.putString("filter", BuiltInRegistries.ITEM.getKey(filter).toString());
        tag.putInt("keep", keep);
        tag.putInt("batch", batch);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        filter = null;
        if (tag.contains("filter")) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("filter"));
            if (id != null) filter = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        }
        keep = Math.max(0, Math.min(MAX_KEEP, tag.getInt("keep")));
        batch = tag.contains("batch") ? Math.max(1, Math.min(64, tag.getInt("batch"))) : 16;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.chottonokosu.chotto_nokosu_pipe");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ChottoNokosuPipeMenu(id, inv, worldPosition, new ContainerData() {
            @Override public int get(int i) {
                return switch (i) {
                    case 0 -> filter == null ? -1 : BuiltInRegistries.ITEM.getId(filter);
                    case 1 -> keep;
                    default -> batch;
                };
            }
            @Override public void set(int i, int v) { }
            @Override public int getCount() { return 3; }
        });
    }
}
