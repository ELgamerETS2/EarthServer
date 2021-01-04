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

	public static void updatePermissions() {

		LuckPerms lp = Main.getLuckPerms();
		MySQL mysql = new MySQL();
		
		while (true) {
			
			String[] entry = mysql.getPermission();
			
			if (entry == null) {
				break;
			}

			String uuid = entry[0];

			if (!uuid.equals("builder")) {

				UserManager userManager = lp.getUserManager();
				CompletableFuture<User> userFuture = userManager.loadUser(UUID.fromString(uuid));

				userFuture.thenAcceptAsync(user -> {

					if (entry[1] != null) {

						String[] add = entry[1].split(";");

						for (int i = 0; i < add.length; i++) {

							PermissionNode node = PermissionNode.builder("worldedit.*")
									.value(true)
									.withContext(ImmutableContextSet.of("wg-region", add[i]))
									.build();

							user.data().add(node);

						}
					}

					if (entry[2] != null) {

						String[] remove = entry[2].split(";");

						for (int j = 0; j < remove.length; j++) {

							PermissionNode node = PermissionNode.builder("worldedit.*")
									.value(true)
									.withContext(ImmutableContextSet.of("wg-region", remove[j]))
									.build();

							user.data().remove(node);
						}
					}

					userManager.saveUser(user);

				});
			} else {

				Group gp = lp.getGroupManager().getGroup(uuid);				

				if (entry[1] != null) {

					String[] add = entry[1].split(";");

					for (int i = 0; i < add.length; i++) {

						PermissionNode node = PermissionNode.builder("worldedit.*")
								.value(true)
								.withContext(ImmutableContextSet.of("wg-region", add[i]))
								.build();

						gp.data().add(node);

					}
				}

				if (entry[2] != null) {

					String[] remove = entry[2].split(";");

					for (int j = 0; j < remove.length; j++) {

						PermissionNode node = PermissionNode.builder("worldedit.*")
								.value(true)
								.withContext(ImmutableContextSet.of("wg-region", remove[j]))
								.build();

						gp.data().remove(node);
					}
				}

				lp.getGroupManager().saveGroup(gp);

			}
		}

	}

}
