package com.ddoerr.scriptit.models.inventory;

import com.ddoerr.scriptit.ScriptItMod;
import com.ddoerr.scriptit.ducks.TooltipRenderedDuck;
import com.ddoerr.scriptit.models.ItemModel;
import com.ddoerr.scriptit.mixin.ContainerAccessor;
import com.ddoerr.scriptit.mixin.CreativeInventoryAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.util.List;

public class CreativeInventoryModel extends ContainerInventoryModel {
    public CreativeInventoryModel(Screen screen, PlayerInventory inventory) {
        super(screen, inventory);
    }

    private void accessSurvivalInventory() {
        ((CreativeInventoryAccessor)screen).invokeSetSelectedTab(ItemGroup.INVENTORY);
    }

    @Override
    public void renderTooltip(int mouseX, int mouseY) {
        Slot focusedSlot = ((ContainerAccessor)screen).getFocusedSlot();

        if (focusedSlot == null) {
            return;
        }

        int slotId = focusedSlot.id;
        if (((CreativeInventoryScreen)screen).getSelectedTab() == ItemGroup.INVENTORY.getIndex()) {
            slotId = ((CreativeInventoryScreen)screen).getContainer().slots.indexOf(focusedSlot);
        }

        if (((TooltipRenderedDuck) screen).wasTooltipRendered()) {
            mouseY -= 18;
        }

        screen.renderTooltip(I18n.translate(new Identifier(ScriptItMod.MOD_NAME, "tooltip.slot").toString(), slotId), mouseX, mouseY);
    }

    @Override
    public void click(int slot, ClickType clickType) {
        super.click(slot, clickType);
    }

    @Override
    public List<ItemModel> getSlots() {
        return super.getSlots();
    }

    @Override
    public ItemModel slot(int slot) {
        return super.slot(slot);
    }

    @Override
    public int getSlotCount() {
        return super.getSlotCount();
    }
}
