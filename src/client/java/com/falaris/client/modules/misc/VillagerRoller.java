package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import java.util.Random;

public class VillagerRoller extends Module {
    public final BooleanSetting onlyWhenTrading = addSetting(new BooleanSetting("Only When Trading", "Only roll while a trade menu is open", true));
    public final BooleanSetting keepRolling = addSetting(new BooleanSetting("Keep Rolling", "Continually refresh trades after finding good ones", true));
    public final NumberSetting rollDelay = addSetting(new NumberSetting("Roll Delay", "Ticks between scroll clicks", 4, 1, 20, 1));

    private int rollTicks = 0;
    private Random random = new Random();

    public VillagerRoller() {
        super("VillagerRoller", "Auto-cycle villager trades to find rare enchants", Category.MISC, "roller");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        if (--rollTicks > 0) return;

        if (onlyWhenTrading.getValue()) {
            if (!(mc.player.containerMenu instanceof MerchantMenu)) return;
        }

        if (random.nextFloat() < 0.02) {
            if (keepRolling.getValue()) {
                rollTicks = getRandomDelay();
                return;
            } else {
                setEnabled(false);
                return;
            }
        }

        rollTicks = getRandomDelay();
    }

    private int getRandomDelay() {
        return (int) Math.max(1.0, rollDelay.getValue() + random.nextInt((int) (rollDelay.getValue() * 2)));
    }
}
