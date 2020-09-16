package me.elgamer.earthserver.utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.elgamer.earthserver.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.DefaultContextKeys;
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
					.withContext(DefaultContextKeys.SERVER_KEY, "worldguard:" + region)
					.build();

			user.data().add(node);

			lp.getUserManager().saveUser(user);

		});

	}

	public void removePermission(UUID uuid, String region) {

		LuckPerms lp = Main.getLuckPerms();

		UserManager userManager = lp.getUserManager();
		CompletableFuture<User> userFuture = userManager.loadUser(uuid);

		userFuture.thenAcceptAsync(user -> {

			PermissionNode node = PermissionNode.builder("worldedit.*")
					.value(true)
					.withContext(DefaultContextKeys.SERVER_KEY, "worldguard:" + region)
					.build();

			user.data().remove(node);

			lp.getUserManager().saveUser(user);

		});

	}

}
