package com.haoling.chottonokosu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/** No item slots; the screen only has buttons, handled through {@link #clickMenuButton}. */
public class ChottoNokosuPipeMenu extends AbstractContainerMenu {
    // Button ids
    public static final int CLEAR_FILTER = 0;
    public static final int KEEP_BASE = 10;   // 10..15 => deltas below
    public static final int[] KEEP_DELTAS = {-64, -10, -1, 1, 10, 64};
    public static final int BATCH_BASE = 20;  // 20..23
    public static final int[] BATCH_DELTAS = {-8, -1, 1, 8};

    private final BlockPos pos;
    private final ContainerData data;

    /** Client side. */
    public ChottoNokosuPipeMenu(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(id, inv, buf.readBlockPos(), new SimpleContainerData(3));
    }

    public ChottoNokosuPipeMenu(int id, Inventory inv, BlockPos pos, ContainerData data) {
        super(ChottoNokosuPipeMod.PIPE_MENU.get(), id);
        this.pos = pos;
        this.data = data;
        addDataSlots(data);
    }

    public int filterRawId() { return data.get(0); }
    public int keep() { return data.get(1); }
    public int batch() { return data.get(2); }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (!(player.level().getBlockEntity(pos) instanceof ChottoNokosuPipeBlockEntity be)) return false;
        if (button == CLEAR_FILTER) {
            be.setFilter(null);
            return true;
        }
        if (button >= KEEP_BASE && button < KEEP_BASE + KEEP_DELTAS.length) {
            be.setKeep(be.getKeep() + KEEP_DELTAS[button - KEEP_BASE]);
            return true;
        }
        if (button >= BATCH_BASE && button < BATCH_BASE + BATCH_DELTAS.length) {
            be.setBatch(be.getBatch() + BATCH_DELTAS[button - BATCH_BASE]);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().getBlockState(pos).is(ChottoNokosuPipeMod.PIPE.get())
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
}
