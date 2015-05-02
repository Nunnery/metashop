/*
 * This file is part of Strife, licensed under the ISC License.
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
package com.tealcube.minecraft.spigot.metashop.managers;

import com.tealcube.minecraft.spigot.metashop.collections.CaselessMap;
import com.tealcube.minecraft.spigot.metashop.common.Preconditions;
import com.tealcube.minecraft.spigot.metashop.shops.Shop;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShopManager {

    private static final Map<String, Shop> SHOP_MAP = new CaselessMap<>();

    private ShopManager() {
        // do nothing
    }

    public static boolean hasShop(String name) {
        Preconditions.checkNotNull(name, "name cannot be null");
        return SHOP_MAP.containsKey(name);
    }

    public static boolean hasShop(Shop shop) {
        Preconditions.checkNotNull(shop, "shop cannot be null");
        return hasShop(shop.getName());
    }

    public static boolean addShop(Shop shop) {
        Preconditions.checkNotNull(shop, "shop cannot be null");
        SHOP_MAP.put(shop.getId(), shop);
        return hasShop(shop);
    }

    public static boolean removeShop(Shop shop) {
        Preconditions.checkNotNull(shop, "shop cannot be null");
        SHOP_MAP.remove(shop.getName());
        return !hasShop(shop);
    }

    public static boolean removeShop(String name) {
        Preconditions.checkNotNull(name, "name cannot be null");
        SHOP_MAP.remove(name);
        return !hasShop(name);
    }

    public static Set<Shop> getShops() {
        return new HashSet<>(SHOP_MAP.values());
    }

    public static Shop getShop(String name) {
        Preconditions.checkNotNull(name);
        return SHOP_MAP.get(name);
    }

}
