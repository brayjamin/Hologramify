/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.v1_17_R1;

import me.filoghost.fcommons.logging.Log;
import me.filoghost.holographicdisplays.common.nms.AbstractNMSPacketList;
import me.filoghost.holographicdisplays.common.nms.EntityID;
import me.filoghost.holographicdisplays.common.nms.IndividualCustomName;
import me.filoghost.holographicdisplays.common.nms.IndividualNMSPacket;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

class VersionNMSPacketList extends AbstractNMSPacketList {

    private static final boolean USE_ENTITY_LIST_DESTROY_PACKET = useEntityListDestroyPacket();

    @Override
    public void addArmorStandSpawnPackets(EntityID entityID, double locationX, double locationY, double locationZ) {
        add(new EntityLivingSpawnNMSPacket(entityID, EntityTypeID.ARMOR_STAND, locationX, locationY, locationZ));
        add(EntityMetadataNMSPacket.builder(entityID)
                .setMarkerArmorStand()
                .build()
        );
    }

    @Override
    public void addArmorStandSpawnPackets(EntityID entityID, double locationX, double locationY, double locationZ, String customName) {
        add(new EntityLivingSpawnNMSPacket(entityID, EntityTypeID.ARMOR_STAND, locationX, locationY, locationZ));
        add(EntityMetadataNMSPacket.builder(entityID)
                .setMarkerArmorStand()
                .setCustomName(customName)
                .build()
        );
    }

    @Override
    public void addArmorStandSpawnPackets(EntityID entityID, double locationX, double locationY, double locationZ, IndividualCustomName individualCustomName) {
        add(new EntityLivingSpawnNMSPacket(entityID, EntityTypeID.ARMOR_STAND, locationX, locationY, locationZ));
        add(new IndividualNMSPacket(player -> EntityMetadataNMSPacket.builder(entityID)
                .setMarkerArmorStand()
                .setCustomName(individualCustomName.get(player))
                .build()
        ));
    }

    @Override
    public void addArmorStandNameChangePackets(EntityID entityID, String customName) {
        add(EntityMetadataNMSPacket.builder(entityID)
                .setCustomName(customName)
                .build()
        );
    }

    @Override
    public void addArmorStandNameChangePackets(EntityID entityID, IndividualCustomName individualCustomName) {
        add(new IndividualNMSPacket(player -> EntityMetadataNMSPacket.builder(entityID)
                .setCustomName(individualCustomName.get(player))
                .build()
        ));
    }

    @Override
    public void addItemSpawnPackets(EntityID entityID, double locationX, double locationY, double locationZ, ItemStack itemStack) {
        add(new EntitySpawnNMSPacket(entityID, EntityTypeID.ITEM, locationX, locationY, locationZ));
        add(EntityMetadataNMSPacket.builder(entityID)
                .setItemStack(itemStack)
                .build()
        );
    }

    @Override
    public void addItemStackChangePackets(EntityID entityID, ItemStack itemStack) {
        add(EntityMetadataNMSPacket.builder(entityID)
                .setItemStack(itemStack)
                .build()
        );
    }

    @Override
    public void addSlimeSpawnPackets(EntityID entityID, double locationX, double locationY, double locationZ) {
        add(new EntityLivingSpawnNMSPacket(entityID, EntityTypeID.SLIME, locationX, locationY, locationZ));
        add(EntityMetadataNMSPacket.builder(entityID)
                .setInvisible()
                .build()
        );
    }

    @Override
    public void addEntityDestroyPackets(EntityID... entityIDs) {
        if (USE_ENTITY_LIST_DESTROY_PACKET) {
            add(new EntityListDestroyNMSPacket(entityIDs));
        } else {
            for (EntityID entityID : entityIDs) {
                add(new EntityDestroyNMSPacket(entityID));
            }
        }
    }

    @Override
    public void addTeleportPackets(EntityID entityID, double locationX, double locationY, double locationZ) {
        add(new EntityTeleportNMSPacket(entityID, locationX, locationY, locationZ));
    }

    @Override
    public void addMountPackets(EntityID vehicleEntityID, EntityID passengerEntityID) {
        add(new EntityMountNMSPacket(vehicleEntityID, passengerEntityID));
    }

    private static boolean useEntityListDestroyPacket() {
        try {
            for (Field field : PacketPlayOutEntityDestroy.class.getDeclaredFields()) {
                if (field.getType() == IntList.class) {
                    return true;
                }
            }
            return false;
        } catch (Throwable t) {
            Log.warning("Could not detect PacketPlayOutEntityDestroy details, error can be ignored if on Minecraft 1.17.1+", t);
            return true; // Assume newer Minecraft version
        }
    }

}
