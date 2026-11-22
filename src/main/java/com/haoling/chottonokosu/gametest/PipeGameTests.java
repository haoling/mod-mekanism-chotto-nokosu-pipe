package com.haoling.chottonokosu.gametest;

import com.haoling.chottonokosu.ChottoNokosuPipeBlock;
import com.haoling.chottonokosu.ChottoNokosuPipeBlockEntity;
import com.haoling.chottonokosu.ChottoNokosuPipeMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ChottoNokosuPipeMod.MODID)
@PrefixGameTestTemplate(false)
public class PipeGameTests {
    private static int count(ChestBlockEntity chest, net.minecraft.world.item.Item item) {
        int n = 0;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            if (chest.getItem(i).is(item)) n += chest.getItem(i).getCount();
        }
        return n;
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void movesOnlySurplus(GameTestHelper helper) {
        BlockPos srcPos = new BlockPos(0, 1, 1), pipePos = new BlockPos(1, 1, 1), dstPos = new BlockPos(2, 1, 1);
        helper.setBlock(srcPos, Blocks.CHEST);
        helper.setBlock(dstPos, Blocks.CHEST);
        helper.setBlock(pipePos, ChottoNokosuPipeMod.PIPE.get().defaultBlockState().setValue(ChottoNokosuPipeBlock.FACING, Direction.EAST));
        ChestBlockEntity src = (ChestBlockEntity) helper.getBlockEntity(srcPos);
        ChestBlockEntity dst = (ChestBlockEntity) helper.getBlockEntity(dstPos);
        src.setItem(0, new ItemStack(Items.COBBLESTONE, 64));
        src.setItem(1, new ItemStack(Items.COBBLESTONE, 36));
        src.setItem(2, new ItemStack(Items.DIRT, 10));
        ((ChottoNokosuPipeBlockEntity) helper.getBlockEntity(pipePos)).setKeep(64); // default, explicit
        helper.runAfterDelay(100, () -> {
            int srcCobble = count(src, Items.COBBLESTONE), dstCobble = count(dst, Items.COBBLESTONE);
            if (srcCobble != 64 || dstCobble != 36) {
                helper.fail("cobble src=" + srcCobble + " dst=" + dstCobble + " (expected 64/36)");
            } else if (count(dst, Items.DIRT) != 0 || count(src, Items.DIRT) != 10) {
                helper.fail("dirt (below threshold) must stay");
            } else {
                helper.succeed();
            }
        });
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void filterOnly(GameTestHelper helper) {
        BlockPos srcPos = new BlockPos(0, 1, 1), pipePos = new BlockPos(1, 1, 1), dstPos = new BlockPos(2, 1, 1);
        helper.setBlock(srcPos, Blocks.CHEST);
        helper.setBlock(dstPos, Blocks.CHEST);
        helper.setBlock(pipePos, ChottoNokosuPipeMod.PIPE.get().defaultBlockState().setValue(ChottoNokosuPipeBlock.FACING, Direction.EAST));
        ChestBlockEntity src = (ChestBlockEntity) helper.getBlockEntity(srcPos);
        ChestBlockEntity dst = (ChestBlockEntity) helper.getBlockEntity(dstPos);
        src.setItem(0, new ItemStack(Items.COBBLESTONE, 30));
        src.setItem(1, new ItemStack(Items.DIRT, 30));
        ChottoNokosuPipeBlockEntity pipe = (ChottoNokosuPipeBlockEntity) helper.getBlockEntity(pipePos);
        pipe.setFilter(Items.DIRT);
        pipe.setKeep(10);
        helper.runAfterDelay(100, () -> {
            if (count(dst, Items.DIRT) != 20 || count(src, Items.DIRT) != 10) {
                helper.fail("dirt src=" + count(src, Items.DIRT) + " dst=" + count(dst, Items.DIRT) + " (expected 10/20)");
            } else if (count(dst, Items.COBBLESTONE) != 0) {
                helper.fail("filtered-out item moved");
            } else {
                helper.succeed();
            }
        });
    }
}
