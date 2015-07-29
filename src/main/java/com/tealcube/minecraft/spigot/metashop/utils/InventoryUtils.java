/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tealcube.minecraft.spigot.metashop.utils;

import com.tealcube.minecraft.spigot.metashop.common.Preconditions;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtils {

    private InventoryUtils() {
        // do nothing
    }

    public static boolean canBeAddedToInventory(Inventory inventory, ItemStack itemStack) {
        Preconditions.checkNotNull(inventory);
        Preconditions.checkNotNull(itemStack);
        int index = inventory.firstEmpty();
        if (index >= 0) {
            return true;
        }
        for (ItemStack is : inventory.all(itemStack.getType()).values()) {
            if (!is.isSimilar(itemStack)) {
                continue;
            }
            if (is.getAmount() + itemStack.getAmount() <= is.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

}
