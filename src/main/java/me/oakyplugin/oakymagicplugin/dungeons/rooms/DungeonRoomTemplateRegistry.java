package me.oakyplugin.oakymagicplugin.dungeons.rooms;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class DungeonRoomTemplateRegistry {

    private HashMap<String, DungeonRoomTemplate> templatesById;

    public DungeonRoomTemplateRegistry() {
        templatesById = new HashMap<>();
    }

    public void loadTemplates() throws IOException {
        File structuresFolder = new File(OakyMagicPlugin.getPluginFolder()+"/structures/dungeons/rooms");

        int structuresLoaded = 0;

        if(structuresFolder.isDirectory()) {
            DungeonRoomType[] types = DungeonRoomType.values();
            for(DungeonRoomType type : types) {
                String roomType = type.name().toLowerCase(Locale.ROOT);
                File structureTypeFolder = new File(structuresFolder, roomType);
                if(structureTypeFolder.isDirectory()) {
                    File[] files = structureTypeFolder.listFiles();
                    if(files == null) continue;
                    for(File f : files) {
                        if(!f.getName().toLowerCase(Locale.ROOT).endsWith(".nbt")) continue;
                        String templateId = roomType+":"+f.getName().substring(0,f.getName().length() - ".nbt".length());
                        templatesById.put(templateId,new DungeonRoomTemplate(type, Bukkit.getStructureManager().loadStructure(f)));
                        Bukkit.getLogger().info(String.format("[oakymagicplugin] Loaded template %s",templateId));
                        structuresLoaded++;
                    }
                }
            }
        }

        Bukkit.getLogger().info("[oakymagicplugin] Loaded "+structuresLoaded+" Dungeom Room Templates");
    }

    public DungeonRoomTemplate getTemplate(String id) {
        return templatesById.get(id);
    }

    public String[] getTemplateIds() {
        String[] ids = new String[templatesById.size()];

        int i = 0;
        for(String s : templatesById.keySet()) {
            ids[i] = s;
            i++;
        }
        return ids;
    }
}
