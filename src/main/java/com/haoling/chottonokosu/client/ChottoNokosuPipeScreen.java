package com.haoling.chottonokosu.client;

import com.haoling.chottonokosu.ChottoNokosuPipeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ChottoNokosuPipeScreen extends AbstractContainerScreen<ChottoNokosuPipeMenu> {
    public ChottoNokosuPipeScreen(ChottoNokosuPipeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 110;
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelY = 10000; // hide "Inventory" label
        int x = leftPos, y = topPos;
        // Keep amount buttons
        String[] keepLabels = {"-64", "-10", "-1", "+1", "+10", "+64"};
        for (int i = 0; i < keepLabels.length; i++) {
            addButton(x + 8 + i * 26, y + 48, 24, keepLabels[i], ChottoNokosuPipeMenu.KEEP_BASE + i);
        }
        // Batch buttons
        String[] batchLabels = {"-8", "-1", "+1", "+8"};
        for (int i = 0; i < batchLabels.length; i++) {
            addButton(x + 8 + i * 26, y + 84, 24, batchLabels[i], ChottoNokosuPipeMenu.BATCH_BASE + i);
        }
        // Clear filter
        addRenderableWidget(Button.builder(Component.translatable("gui.chottonokosu.clear_filter"),
                        b -> click(ChottoNokosuPipeMenu.CLEAR_FILTER))
                .bounds(x + 100, y + 20, 68, 20).build());
    }

    private void addButton(int x, int y, int w, String label, int id) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> click(id)).bounds(x, y, w, 20).build());
    }

    private void click(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        g.renderOutline(leftPos, topPos, imageWidth, imageHeight, 0xFF555555);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, 8, 6, 0x404040, false);
        int raw = menu.filterRawId();
        Item item = raw >= 0 ? BuiltInRegistries.ITEM.byId(raw) : null;
        Component filterName = item == null
                ? Component.translatable("gui.chottonokosu.any_item")
                : new ItemStack(item).getHoverName();
        if (item != null) g.renderItem(new ItemStack(item), 8, 20);
        g.drawString(font, filterName, 28, 24, 0x404040, false);
        g.drawString(font, Component.translatable("gui.chottonokosu.keep", menu.keep()), 8, 38, 0x404040, false);
        g.drawString(font, Component.translatable("gui.chottonokosu.batch", menu.batch()), 8, 74, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }
}
