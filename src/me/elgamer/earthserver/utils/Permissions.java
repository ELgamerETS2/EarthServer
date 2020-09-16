package me.elgamer.earthserver.utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.elgamer.earthserver.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;


public class Permissions {

	public void addPermission(UUID uuid, String region) {

		LuckPerms lp = Main.getLuckPerms();

		UserManager userManager = lp.getUserManager();
		CompletableFuture<User> userFuture = userManager.loadUser(uuid);

		userFuture.thenAcceptAsync(user -> {

			PermissionNode node = PermissionNode.builder("worldedit.*")
					.value(true)
					.withContext(ImmutableContextSet.of("wg-region", region))
					.build();

			user.data().add(node);

			userManager.saveUser(user);

		});

	}

	public void removePermission(UUID uuid, String region) {

		LuckPerms lp = Main.getLuckPerms();

		UserManager userManager = lp.getUserManager();
		CompletableFuture<User> userFuture = userManager.loadUser(uuid);

		userFuture.thenAcceptAsync(user -> {

			PermissionNode node = PermissionNode.builder("worldedit.*")
					.value(true)
					.withContext(ImmutableContextSet.of("wg-region", region))
					.build();

			user.data().remove(node);

			userManager.saveUser(user);

		});

	}

	public void addGroupPermission(String group, String region) {

		LuckPerms lp = Main.getLuckPerms();

		Group gp = lp.getGroupManager().getGroup(group);

		PermissionNode node = PermissionNode.builder("worldedit.*")
				.value(true)
				.withContext(ImmutableContextSet.of("wg-region", region))
				.build();

		gp.data().add(node);

		lp.getGroupManager().saveGroup(gp);

	}

	public void removeGroupPermission(String group, String region) {

		LuckPerms lp = Main.getLuckPerms();

		Group gp = lp.getGroupManager().getGroup(group);

		PermissionNode node = PermissionNode.builder("worldedit.*")
				.value(true)
				.withContext(ImmutableContextSet.of("wg-region", region))
				.build();

		gp.data().remove(node);

		lp.getGroupManager().saveGroup(gp);

	}

}
