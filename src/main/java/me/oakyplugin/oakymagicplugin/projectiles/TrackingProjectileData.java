package me.oakyplugin.oakymagicplugin.projectiles;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class TrackingProjectileData {

    private final double turnSpeed;
    private final double accelMagnitude;
    private final double dragCoeff;
    private final double trackingDistance;

    public TrackingProjectileData(double turnSpeed, double accelMagnitude, double dragCoeff, double trackingDistance) {
        this.turnSpeed = turnSpeed;
        this.accelMagnitude = accelMagnitude;
        this.dragCoeff = dragCoeff;
        this.trackingDistance = trackingDistance;
    }

    public double getTurnSpeed() {return turnSpeed;}
    public double getAccelMagnitude() {return accelMagnitude;}
    public double getDragCoeff() {return dragCoeff;}
    public double getTrackingDistance() {return trackingDistance;}

    public static TurnSpeed builder() { return new TrackingProjectileDataBuilder(); }
    public interface TurnSpeed { AccelMagnitude turnSpeed(double x); }
    public interface AccelMagnitude { DragCoeff acceleration(double x); }
    public interface DragCoeff { TrackingDistance dragCoefficient(double x); }
    public interface TrackingDistance extends Build { Build trackingDistance(double x);}
    public interface Build { TrackingProjectileData build(); }

    public static final class TrackingProjectileDataBuilder implements TurnSpeed, AccelMagnitude, DragCoeff, Build, TrackingDistance {
        private double turnSpeed;
        private double accelMagnitude;
        private double dragCoeff;
        private double trackingDistance;

        private TrackingProjectileDataBuilder() {
            trackingDistance = -1;
        }

        @Override
        public TrackingProjectileData build() {
            return new TrackingProjectileData(turnSpeed, accelMagnitude, dragCoeff, trackingDistance);
        }

        @Override
        public DragCoeff acceleration(double x) {
            accelMagnitude = x;
            return this;
        }

        /// 1 = infinite drag, 0 = no drag
        @Override
        public TrackingDistance dragCoefficient(double x) {
            dragCoeff = Math.clamp(x, 0, 1);
            return this;
        }

        /// In radians per tick
        @Override
        public AccelMagnitude turnSpeed(double x) {
            turnSpeed = x;
            return this;
        }

        @Override
        public Build trackingDistance(double x) {
            trackingDistance = x;
            return this;
        }
    }
}
