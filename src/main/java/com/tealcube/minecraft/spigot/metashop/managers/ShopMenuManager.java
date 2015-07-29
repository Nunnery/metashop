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
package com.tealcube.minecraft.spigot.metashop.managers;

import com.tealcube.minecraft.spigot.metashop.collections.CaselessMap;
import com.tealcube.minecraft.spigot.metashop.common.Preconditions;
import com.tealcube.minecraft.spigot.metashop.shops.ShopMenu;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShopMenuManager {

    private static final Map<String, ShopMenu> SHOP_MAP = new CaselessMap<>();

    private ShopMenuManager() {
        // do nothing
    }

    public static boolean hasShop(String name) {
        Preconditions.checkNotNull(name, "name cannot be null");
        return SHOP_MAP.containsKey(name);
    }

    public static boolean hasShop(ShopMenu shopMenu) {
        Preconditions.checkNotNull(shopMenu, "shop cannot be null");
        return hasShop(shopMenu.getName());
    }

    public static boolean addShop(ShopMenu shopMenu) {
        Preconditions.checkNotNull(shopMenu, "shop cannot be null");
        SHOP_MAP.put(shopMenu.getId(), shopMenu);
        return hasShop(shopMenu);
    }

    public static boolean removeShop(ShopMenu shopMenu) {
        Preconditions.checkNotNull(shopMenu, "shop cannot be null");
        SHOP_MAP.remove(shopMenu.getName());
        return !hasShop(shopMenu);
    }

    public static boolean removeShop(String name) {
        Preconditions.checkNotNull(name, "name cannot be null");
        SHOP_MAP.remove(name);
        return !hasShop(name);
    }

    public static Set<ShopMenu> getShops() {
        return new HashSet<>(SHOP_MAP.values());
    }

    public static ShopMenu getShop(String name) {
        Preconditions.checkNotNull(name);
        return SHOP_MAP.get(name);
    }

}
