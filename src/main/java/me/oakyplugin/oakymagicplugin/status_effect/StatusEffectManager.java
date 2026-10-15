package me.oakyplugin.oakymagicplugin.status_effect;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.EntityManager;
import me.oakyplugin.oakymagicplugin.entity.PuppetSpellEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;

import java.util.*;

public class StatusEffectManager {

    private final Map<UUID, Set<StatusEffect>> entityStatusEffects = new HashMap<>();
    private final Set<UUID> entityStatusEffectsToRemove = new HashSet<>();

    public void tick() {
        for(UUID uuid : entityStatusEffects.keySet()) {
            Entity entity = Bukkit.getEntity(uuid);
            if(!(entity instanceof LivingEntity)) {
                entityStatusEffectsToRemove.add(uuid);
                continue;
            }

            Set<StatusEffect> effects = entityStatusEffects.get(uuid);
            Set<StatusEffect> remove = new HashSet<>();
            for(StatusEffect effect : effects) {
                effect.tick();
                if(!effect.getIsActive()) remove.add(effect);
            }

            effects.removeAll(remove);
            if(effects.isEmpty()) entityStatusEffectsToRemove.add(uuid);

        }
        for(UUID uuid : entityStatusEffectsToRemove)
            entityStatusEffects.remove(uuid);
        entityStatusEffectsToRemove.clear();
    }

    public void inflict(UUID uuid, StatusEffect newEffect) {
        Entity entity = Bukkit.getEntity(uuid);
        if(!(entity instanceof LivingEntity)) return;

        if(entity instanceof Mannequin m) {
            Bukkit.broadcast(Component.text("Is mannequin"));
            EntityManager em = OakyMagicPlugin.getPlugin().getEntityManager();
            PuppetSpellEntity pse = em.getPuppetFromMannequin(m);
            if(pse != null) {
                newEffect.setTarget(pse.getOwner().getUniqueId());
                uuid = pse.getOwner().getUniqueId();
                Bukkit.broadcast(Component.text("Mannequin is PSE"));
            }
        }


        Set<StatusEffect> effects = entityStatusEffects.computeIfAbsent(uuid, k -> new HashSet<>());
        entityStatusEffects.put(uuid, effects);

        for(StatusEffect effect : effects) {
            if(effect.getClass() == newEffect.getClass()) {
                effect.merge(newEffect);
                return;
            }
        }

        effects.add(newEffect);
    }
    public void clearStatusEffects(UUID entity) {
        if(!entityStatusEffects.containsKey(entity)) return;
        for (StatusEffect effect : entityStatusEffects.get(entity)) {
            effect.end();
        }
        entityStatusEffects.remove(entity);
    }
}
