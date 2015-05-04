/*
 * This file is part of MetaShop, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
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
