package com.samsthenerd.hexgloop.blockentities;

import java.util.Random;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.blocks.BlockConjured;
import at.petrak.hexcasting.common.blocks.BlockConjuredLight;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockEntityConjuredRedstone extends HexBlockEntity{
    private static final Random RANDOM = new Random();
    private FrozenColorizer colorizer = FrozenColorizer.DEFAULT.get();
    private int power = 0;

    public static final String TAG_COLORIZER = "tag_colorizer";
    public static final String TAG_POWER = "tag_power";

    public BlockEntityConjuredRedstone(BlockPos pos, BlockState state) {
        super(HexGloopBEs.CONJURED_REDSTONE_BE.get(), pos, state);
    }

    public void walkParticle(Entity pEntity) {
        if (getCachedState().getBlock() instanceof BlockConjured conjured && !(conjured instanceof BlockConjuredLight)) {
            for (int i = 0; i < 3; ++i) {
                int color = this.colorizer.getColor(pEntity.age, pEntity.getPos()
                    .add(new Vec3d(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()).multiply(
                        RANDOM.nextFloat() * 3)));
                assert world != null;
                world.addParticle(new ConjureParticleOptions(color, false),
                    pEntity.getX() + (RANDOM.nextFloat() * 0.6D) - 0.3D,
                    getPos().getY() + (RANDOM.nextFloat() * 0.05D) + 0.95D,
                    pEntity.getZ() + (RANDOM.nextFloat() * 0.6D) - 0.3D,
                    RANDOM.nextFloat(-0.02f, 0.02f),
                    RANDOM.nextFloat(0.02f),
                    RANDOM.nextFloat(-0.02f, 0.02f));
            }
        }
    }

    public void particleEffect() {
        if (getCachedState().getBlock() instanceof BlockConjured) {
            int color = this.colorizer.getColor(RANDOM.nextFloat() * 16384,
                new Vec3d(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()).multiply(
                    RANDOM.nextFloat() * 3));
            assert world != null;
            if (getCachedState().getBlock() instanceof BlockConjuredLight) {
                if (RANDOM.nextFloat() < 0.5) {
                    world.addParticle(new ConjureParticleOptions(color, true),
                        (double) getPos().getX() + 0.45D + (RANDOM.nextFloat() * 0.1D),
                        (double) getPos().getY() + 0.45D + (RANDOM.nextFloat() * 0.1D),
                        (double) getPos().getZ() + 0.45D + (RANDOM.nextFloat() * 0.1D),
                        RANDOM.nextFloat(-0.005f, 0.005f),
                        RANDOM.nextFloat(-0.002f, 0.02f),
                        RANDOM.nextFloat(-0.005f, 0.005f));
                }
            } else {
                if (RANDOM.nextFloat() < 0.2) {
                    world.addParticle(new ConjureParticleOptions(color, false),
                        (double) getPos().getX() + RANDOM.nextFloat(),
                        (double) getPos().getY() + RANDOM.nextFloat(),
                        (double) getPos().getZ() + RANDOM.nextFloat(),
                        RANDOM.nextFloat(-0.02f, 0.02f),
                        RANDOM.nextFloat(-0.02f, 0.02f),
                        RANDOM.nextFloat(-0.02f, 0.02f));
                }
            }
        }
    }

    public void landParticle(Entity entity, int number) {
        for (int i = 0; i < number * 2; i++) {
            int color = this.colorizer.getColor(entity.age, entity.getPos()
                .add(new Vec3d(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()).multiply(
                    RANDOM.nextFloat() * 3)));
            assert world != null;
            world.addParticle(new ConjureParticleOptions(color, false),
                entity.getX() + (RANDOM.nextFloat() * 0.8D) - 0.2D,
                getPos().getY() + (RANDOM.nextFloat() * 0.05D) + 0.95D,
                entity.getZ() + (RANDOM.nextFloat() * 0.8D) - 0.2D,
                0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void saveModData(NbtCompound tag) {
        tag.put(TAG_COLORIZER, this.colorizer.serializeToNBT());
        tag.putInt(TAG_POWER, this.power);
    }

    @Override
    protected void loadModData(NbtCompound tag) {
        this.colorizer = FrozenColorizer.fromNBT(tag.getCompound(TAG_COLORIZER));
        this.power = tag.getInt(TAG_POWER);
    }

    public FrozenColorizer getColorizer() {
        return this.colorizer;
    }

    public void setColorizer(FrozenColorizer colorizer) {
        this.colorizer = colorizer;
        this.sync();
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
        this.sync();
    }
}
