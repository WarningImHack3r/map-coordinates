package me.trruki.mapCoordinates.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.MapDecorations;

import java.util.Objects;

public class MapCoordinatesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("mapcoords")
                    .executes(context -> {
                        Minecraft client = Minecraft.getInstance();
                        if (client.player != null){
                            ItemStack mainItem = client.player.getInventory().getSelectedItem();
                            ItemStack offItem = client.player.getOffhandItem();
                            if (!mainItem.is(Items.FILLED_MAP) && !offItem.is(Items.FILLED_MAP)){
                                client.gui.chatListener().handleSystemMessage(Component.literal("§f[§dTreasure Map Coordinates§f] §cYou are not holding a treasure map"), false);
                            } else if ((mainItem.get(DataComponents.MAP_DECORATIONS) == null || mainItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray().length == 0) && (offItem.get(DataComponents.MAP_DECORATIONS) == null || offItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray().length == 0)) {
                                client.gui.chatListener().handleSystemMessage(Component.literal("§f[§dTreasure Map Coordinates§f] §cYou are holding a map, but it's not a treasure map"), false);
                            } else if (mainItem.get(DataComponents.MAP_DECORATIONS) != null && mainItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray().length != 0 && offItem.get(DataComponents.MAP_DECORATIONS) != null && offItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray().length != 0){
                                MapDecorations.Entry decoration = (MapDecorations.Entry) mainItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray()[0];
                                MapDecorations.Entry decoration2 = (MapDecorations.Entry) offItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray()[0];
                                int x = (int) decoration.x();
                                int z = (int) decoration.z();
                                int x2 = (int) decoration2.x();
                                int z2 = (int) decoration2.z();
                                client.gui.chatListener().handleSystemMessage(Component.literal("§f[§dTreasure Map Coordinates§f] §fMain hand: §aX: "+x+" §8| §aZ: "+z+" §8| §fOff hand: §aX: "+x2+" §8| §aZ: "+z2).setStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard("Main hand: X: "+x+" Z: "+z+" | Off hand: X: "+x2+" Z: "+z2)).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click").setStyle(Style.EMPTY.withColor(TextColor.GREEN))))), false);
                            } else if (mainItem.get(DataComponents.MAP_DECORATIONS) != null && mainItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray().length != 0){
                                MapDecorations.Entry decoration = (MapDecorations.Entry) mainItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray()[0];
                                int x = (int) decoration.x();
                                int z = (int) decoration.z();
                                client.gui.chatListener().handleSystemMessage(Component.literal("§f[§dTreasure Map Coordinates§f] §aX: "+x+" §8| §aZ: "+z).setStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard("X: "+x+" Z: "+z)).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click").setStyle(Style.EMPTY.withColor(TextColor.GREEN))))), false);
                            } else if (offItem.get(DataComponents.MAP_DECORATIONS) != null && offItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray().length != 0){
                                MapDecorations.Entry decoration = (MapDecorations.Entry) offItem.get(DataComponents.MAP_DECORATIONS).decorations().values().toArray()[0];
                                int x = (int) decoration.x();
                                int z = (int) decoration.z();
                                client.gui.chatListener().handleSystemMessage(Component.literal("§f[§dTreasure Map Coordinates§f] §aX: "+x+" §8| §aZ: "+z).setStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard("X: "+x+" Z: "+z)).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click").setStyle(Style.EMPTY.withColor(TextColor.GREEN))))), false);
                            }
                        }
                        return 1;
                    }));
        });
    }
}