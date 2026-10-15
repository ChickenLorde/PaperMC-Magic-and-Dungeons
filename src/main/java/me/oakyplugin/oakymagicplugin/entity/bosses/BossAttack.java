package me.oakyplugin.oakymagicplugin.entity.bosses;

public abstract class BossAttack {

    protected final BossEntity boss;

    private final int attackDuration;
    private int progress;
    private final int attackInterval;
    private final int downTimeTicks;
    private int cooldownTicks;
    private final int maxCooldown;


    private boolean isActive;

    public BossAttack(BossEntity boss, int attackDuration, int downTimeTicks, int maxCooldown) {
        this.boss = boss;
        this.attackDuration = attackDuration;
        this.downTimeTicks = downTimeTicks;
        progress = 0;
        attackInterval = attackDuration;
        this.maxCooldown = maxCooldown;
    }
    public BossAttack(BossEntity boss, int attackDuration, int downTimeTicks, int attackInterval, int maxCooldown) {
        this.boss = boss;
        this.attackDuration = attackDuration;
        this.downTimeTicks = downTimeTicks;
        this.attackInterval = attackInterval;
        progress = 0;
        this.maxCooldown = maxCooldown;
    }

    public boolean activateAttack() {
        if(cooldownTicks > 0 || !precondition()) return false;
        boss.activeAttack = this;
        isActive = true;
        progress = 0;
        return true;
    }

    public final void cancelAttack() {
        if(boss.activeAttack == this) boss.activeAttack = null;
        cooldownTicks = maxCooldown;
        isActive = false;
        progress = 0;
        boss.attackCooldown = downTimeTicks;
    }

    public final void attackTick() {
        if(progress == 0)
            onAttackStart();
        progress++;
        chargeTick();
        if(progress % attackInterval == 0) {
            onAttack();
        }
        if(progress >= attackDuration) {
            onAttackEnd();
            cancelAttack();
        }
    }
    protected boolean precondition() { return true; }
    protected void onAttackStart() {}
    protected void chargeTick() {}
    protected abstract void onAttack();
    protected void onAttackEnd() {}

    protected void reduceCooldown() {
        if(cooldownTicks > 0) cooldownTicks--;
    }
}
