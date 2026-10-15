package me.oakyplugin.oakymagicplugin.entity.bosses.elite_necromancer;

import me.oakyplugin.oakymagicplugin.OakyMagicPlugin;
import me.oakyplugin.oakymagicplugin.entity.bosses.BossAttack;
import me.oakyplugin.oakymagicplugin.entity.bosses.BossEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class SummonMinionsAttack extends BossAttack {

    private final HashSet<LivingEntity> minions = new HashSet<>();

    private final NamespacedKey modifier = new NamespacedKey(OakyMagicPlugin.NAMESPACE, "elite_necromancer.summon_minions_modifier");

    public SummonMinionsAttack(BossEntity boss) {
        super(boss, 50, 60, 400);
    }


    @Override
    protected void onAttack() {
        for(LivingEntity e : new HashSet<>(minions)) {
            if(e == null || !e.isValid())
                minions.remove(e);
        }
        LivingEntity bossEntity = boss.getEntity();
        for(int i = 0; i < 3; i ++) {

            if(minions.size() >= 6) break;

            int rand = (int)(Math.random() * 2);

            if(rand == 0) {
                Zombie zombie = bossEntity.getWorld().createEntity(bossEntity.getLocation(), Zombie.class);
                zombie.customName(Component.text("Reanimate"));
                AttributeInstance speed = zombie.getAttribute(Attribute.MOVEMENT_SPEED);
                speed.setBaseValue(0.26);
                AttributeInstance hp = zombie.getAttribute(Attribute.MAX_HEALTH);
                hp.setBaseValue(8);
                AttributeInstance scale = zombie.getAttribute(Attribute.SCALE);
                scale.setBaseValue(0.85);
                AttributeInstance damage = zombie.getAttribute(Attribute.ATTACK_DAMAGE);
                damage.setBaseValue(2.5);

                zombie.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.noExpDrops, PersistentDataType.BOOLEAN, true);
                zombie.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.removeOnDisable, PersistentDataType.BOOLEAN, true);
                zombie.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.spawnOverrideDisabled, PersistentDataType.BOOLEAN, true);
                zombie.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.noItemDrops, PersistentDataType.BOOLEAN, true);

                zombie.spawnAt(bossEntity.getLocation(), CreatureSpawnEvent.SpawnReason.REINFORCEMENTS);
                minions.add(zombie);
            } else {
                Skeleton skeleton = bossEntity.getWorld().createEntity(bossEntity.getLocation(), Skeleton.class);
                skeleton.customName(Component.text("Reanimate"));
                AttributeInstance speed = skeleton.getAttribute(Attribute.MOVEMENT_SPEED);
                speed.setBaseValue(0.26);
                AttributeInstance hp = skeleton.getAttribute(Attribute.MAX_HEALTH);
                hp.setBaseValue(8);
                AttributeInstance scale = skeleton.getAttribute(Attribute.SCALE);
                scale.setBaseValue(0.85);
                AttributeInstance damage = skeleton.getAttribute(Attribute.ATTACK_DAMAGE);
                damage.setBaseValue(2.5);

                skeleton.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.noExpDrops, PersistentDataType.BOOLEAN, true);
                skeleton.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.removeOnDisable, PersistentDataType.BOOLEAN, true);
                skeleton.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.spawnOverrideDisabled, PersistentDataType.BOOLEAN, true);
                skeleton.getPersistentDataContainer().set(OakyMagicPlugin.SpecialEntityKeys.noItemDrops, PersistentDataType.BOOLEAN, true);

                skeleton.spawnAt(bossEntity.getLocation(), CreatureSpawnEvent.SpawnReason.REINFORCEMENTS);
                minions.add(skeleton);
            }
        }
    }

    @Override
    protected void onAttackStart() {
        LivingEntity e = boss.getEntity();
        e.setVelocity(new Vector(0,0,0));

        if(e instanceof Mob m) {
            m.setAware(false);
        }

        AttributeInstance mvmt = e.getAttribute(Attribute.MOVEMENT_SPEED);
        if(mvmt != null) {
            if(mvmt.getModifier(modifier) == null)
                mvmt.addModifier(new AttributeModifier(modifier, -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }
    }

    @Override
    protected void onAttackEnd() {
        LivingEntity e = boss.getEntity();

        if(e instanceof Mob m) {
            m.setAware(true);
        }

        AttributeInstance mvmt = e.getAttribute(Attribute.MOVEMENT_SPEED);
        if(mvmt != null) {
            if(mvmt.getModifier(modifier) != null)
                mvmt.removeModifier(modifier);
        }
    }

    @Override
    protected void chargeTick() {
        super.chargeTick();
    }

    @Override
    protected boolean precondition() {
        for(LivingEntity e : new HashSet<>(minions)) {
            if(e == null || !e.isValid())
                minions.remove(e);
        }
        return minions.size() <= 3;
    }
}
