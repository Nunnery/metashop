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
package com.tealcube.minecraft.spigot.metashop.managers;

import com.tealcube.minecraft.spigot.metashop.common.Preconditions;
import com.tealcube.minecraft.spigot.metashop.sessions.ShopEditSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SessionManager {

    private static final Map<UUID, ShopEditSession> SHOP_EDIT_SESSION_MAP = new HashMap<>();

    private SessionManager() {
        // do nothing
    }

    public static boolean addShopEditSession(ShopEditSession shopEditSession) {
        Preconditions.checkNotNull(shopEditSession);
        SHOP_EDIT_SESSION_MAP.put(shopEditSession.getOwner(), shopEditSession);
        return hasShopEditSession(shopEditSession);
    }

    public static boolean hasShopEditSession(ShopEditSession shopEditSession) {
        Preconditions.checkNotNull(shopEditSession);
        return SHOP_EDIT_SESSION_MAP.containsKey(shopEditSession.getOwner());
    }

    public static boolean hasShopEditSession(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return SHOP_EDIT_SESSION_MAP.containsKey(uuid);
    }

    public static boolean removeShopEditSession(ShopEditSession shopEditSession) {
        Preconditions.checkNotNull(shopEditSession);
        SHOP_EDIT_SESSION_MAP.remove(shopEditSession.getOwner());
        return !hasShopEditSession(shopEditSession);
    }

    public static boolean removeShopEditSession(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        SHOP_EDIT_SESSION_MAP.remove(uuid);
        return !hasShopEditSession(uuid);
    }

    public static ShopEditSession getShopEditSession(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return SHOP_EDIT_SESSION_MAP.get(uuid);
    }

}
