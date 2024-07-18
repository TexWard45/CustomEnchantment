package me.texward.customenchantment.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.bonus.EntityTypeBonus;
import me.texward.mobslayer.object.SlayerMob;
import me.texward.mobslayer.object.SlayerPlayer;
import me.texward.texwardlib.api.EconomyAPI;
import me.texward.texwardlib.util.SetExpFix;
import uk.antiperson.stackmob.events.StackDeathEvent;

public class MobStackDeathListener implements Listener {
	public CustomEnchantment main;

	public MobStackDeathListener(CustomEnchantment main) {
		Bukkit.getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMobDeath(StackDeathEvent e) {
		LivingEntity dead = e.getEntityDeathEvent().getEntity();
		Player killer = dead.getKiller();
		if (killer != null) {
			CEPlayer cePlayer = CEAPI.getCEPlayer(killer);
			EntityType entityType = dead.getType();

			EntityTypeBonus xpBonus = cePlayer.getMobBonus().getExpBonus();
			if (!xpBonus.isEmpty()) {
				int xp = (int) xpBonus.getBonus(entityType, e.getDeathStep());
				SetExpFix.setTotalExperience(killer, SetExpFix.getTotalExperience(killer) + xp);
			}
			
			EntityTypeBonus mBonus = cePlayer.getMobBonus().getMoneyBonus();
			if (!mBonus.isEmpty()) {
				EconomyAPI.giveMoneyLater(killer, mBonus.getBonus(entityType, e.getDeathStep()));
			}

			EntityTypeBonus msExpBonus = cePlayer.getMobBonus().getMobSlayerExpBonus();
			if (!msExpBonus.isEmpty()) {

				SlayerMob sMob = SlayerMob.getSlayerMob(entityType);
				if (sMob == null) {
					return;
				}

				SlayerPlayer sPlayer = SlayerPlayer.getSlayer(killer);
				if (!sPlayer.isUnlockMob(sMob)) {
					return;
				}

				int currentLevel = sPlayer.getLevel(entityType);
				double xpPlayer = sMob.getExpBonus(currentLevel) * msExpBonus.getBonus(entityType, 1)
						* e.getDeathStep();
				killer.giveExp((int) xpPlayer);
			}
		}
	}
}