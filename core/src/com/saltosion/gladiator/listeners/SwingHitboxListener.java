package com.saltosion.gladiator.listeners;

import java.util.ArrayList;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Sound;
import com.saltosion.gladiator.components.CCombat;
import com.saltosion.gladiator.components.CPhysics;
import com.saltosion.gladiator.systems.CombatSystem;
import com.saltosion.gladiator.util.AppUtil;
import com.saltosion.gladiator.util.AudioLoader;
import com.saltosion.gladiator.util.Direction;
import com.saltosion.gladiator.util.Name;

public class SwingHitboxListener implements CollisionListener {

	private final ArrayList<Entity> hitEntities = new ArrayList<Entity>();
	private final ComponentMapper<CCombat> cm = ComponentMapper.getFor(CCombat.class);
	private final ComponentMapper<CPhysics> pm = ComponentMapper.getFor(CPhysics.class);
	private final Entity source;
	private final Direction direction;

	public SwingHitboxListener(Entity source, Direction direction) {
		this.source = source;
		this.direction = direction;
	}

	@Override
	public void collision(Direction side, Entity host, Entity other) {
		if (other.equals(source) || hitEntities.contains(other)) {
			return; // These entities don't need to take damage
		}
		hitEntities.add(other);

		CCombat otherCombat = cm.get(other);
		if (otherCombat != null) {
			int damage = cm.get(source).getDamage();
			CombatSystem.dealDamage(source, other, damage);
		}

		CPhysics otherPhysics = pm.get(other);
		if (otherPhysics != null && otherPhysics.getCollisionListener() != null
				&& otherPhysics.getCollisionListener() instanceof SwingHitboxListener) {
			float x = 0;
			if (direction == Direction.LEFT) {
				x = 1;
			} else if (direction == Direction.RIGHT) {
				x = -1;
			}
			float force = cm.get(source).getSwingForce();
			pm.get(source).setSimVelocity(x * force, 0);
			
			Sound s = AppUtil.jukebox.returnRandomSound(AudioLoader.getSound(Name.SOUND_CLANG01),
					AudioLoader.getSound(Name.SOUND_CLANG02),
					AudioLoader.getSound(Name.SOUND_CLANG03),
					AudioLoader.getSound(Name.SOUND_CLANG04));
			s.play(AppUtil.sfxVolume);
		}
	}

}
