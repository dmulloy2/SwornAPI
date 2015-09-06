/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.integration;

import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.ListUtil;
import net.dmulloy2.util.Util;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * Standard Vault integration handler
 * @author dmulloy2
 */

public class VaultHandler extends DependencyProvider<Vault>
{
	private Permission perm;
	private Economy econ;
	private Chat chat;

	public VaultHandler(SwornPlugin handler)
	{
		super(handler, "Vault");
	}

	@Override
	public void onEnable()
	{
		ServicesManager sm = handler.getServer().getServicesManager();

		RegisteredServiceProvider<Permission> permProvider = sm.getRegistration(Permission.class);
		if (permProvider != null)
			perm = permProvider.getProvider();

		RegisteredServiceProvider<Economy> econProvider = sm.getRegistration(Economy.class);
		if (econProvider != null)
			econ = econProvider.getProvider();

		RegisteredServiceProvider<Chat> chatProvider = sm.getRegistration(Chat.class);
		if (chatProvider != null)
			chat = chatProvider.getProvider();
	}

	@Override
	public void onDisable()
	{
		perm = null;
		econ = null;
		chat = null;
	}

	// ---- Economy Methods

	public Economy getEconomy()
	{
		return econ;
	}

	public boolean isEconPresent()
	{
		return econ != null;
	}

	/**
	 * Attempts to deposit a given amount into a given Player's balance. Returns
	 * null if the transaction was a success.
	 * 
	 * @param player Player to give money to
	 * @param amount Amount to give
	 * @return Error message, if applicable
	 */
	public String depositPlayer(Player player, double amount)
	{
		return deposit(player.getName(), amount);
	}

	/**
	 * Attempts to deposit a given amount into a given account's balance. Returns
	 * null if the transaction was a success.
	 * 
	 * @param account Account to give money to
	 * @param amount Amount to give
	 * @return Error message, if applicable
	 */
	public String deposit(String account, double amount)
	{
		if (econ == null)
			return "Economy is disabled.";

		try
		{
			@SuppressWarnings("deprecation")
			EconomyResponse response = econ.depositPlayer(account, amount);
			return response.transactionSuccess() ? null : response.errorMessage;
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "deposit({0}, {1})", account, amount));
			return ex.toString();
		}
	}

	/**
	 * Attempts to withdraw a given amount from a given Player's balance.
	 * Returns null if the transaction was a success.
	 * 
	 * @param player Player to take money from
	 * @param amount Amount to take
	 * @return Error message, if applicable
	 */
	public String withdrawPlayer(Player player, double amount)
	{
		return withdraw(player.getName(), amount);
	}

	/**
	 * Attempts to withdraw a given amount from a given account's balance.
	 * Returns null if the transaction was a success.
	 * 
	 * @param account Account to take money from
	 * @param amount Amount to take
	 * @return Error message, if applicable
	 */
	public String withdraw(String account, double amount)
	{
		if (econ == null)
			return "Economy is disabled.";

		try
		{
			@SuppressWarnings("deprecation")
			EconomyResponse response = econ.withdrawPlayer(account, amount);
			return response.transactionSuccess() ? null : response.errorMessage;
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "withdraw({0}, {1})", account, amount));
			return ex.toString();
		}
	}

	/**
	 * Whether or not a Player has a given amount.
	 * 
	 * @param player Player to check
	 * @param amount Amount to check for
	 * @return True if they do, false if not
	 */
	public boolean has(Player player, double amount)
	{
		return has(player.getName(), amount);
	}

	/**
	 * Whether or not an account has a given amount.
	 * 
	 * @param account Account to check
	 * @param amount Amount to check for
	 * @return True if they do, false if not
	 */
	public boolean has(String account, double amount)
	{
		if (econ == null)
			return false;

		try
		{
			@SuppressWarnings("deprecation")
			double balance = econ.getBalance(account);
			return balance >= amount;
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "has({0}, {1})", account, amount));
			return false;
		}
	}

	/**
	 * Whether or not an account exists.
	 * 
	 * @param account Account to check for
	 * @return True if it exists, false if not
	 */
	@SuppressWarnings("deprecation")
	public boolean hasAccount(String account)
	{
		if (econ == null)
			return false;

		return econ.hasAccount(account);
	}

	/**
	 * Formats a given cash amount using the appropriate currency symbol.
	 * 
	 * @param amount Amount to format
	 * @return The formatted string
	 */
	public String format(double amount)
	{
		if (econ == null)
			return Double.toString(amount);

		try
		{
			return econ.format(amount);
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "format({0})", amount));
			return Double.toString(amount);
		}
	}

	/**
	 * Gets a Player's balance.
	 * 
	 * @param player Player to get balance of
	 * @return Balance
	 */
	@SuppressWarnings("deprecation")
	public double getBalance(Player player)
	{
		if (econ == null)
			return -1.0D;

		try
		{
			return econ.getBalance(player.getName());
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "getBalance({0})", player.getName()));
			return -1.0D;
		}
	}

	// ---- Permission Methods

	public Permission getPermission()
	{
		return perm;
	}

	public boolean isPermPresent()
	{
		return perm != null;
	}

	/**
	 * Whether or not a CommandSender has a permission.
	 * 
	 * @param sender Sender to check
	 * @param permission Permission to check for
	 * @return True if they do, false if not
	 */
	public boolean hasPermission(CommandSender sender, String permission)
	{
		if (perm == null)
			return false;

		return perm.has(sender, permission);
	}

	/**
	 * Gives a player a permission.
	 * 
	 * @param player Player to give permission to
	 * @param permission Permission to give
	 * @return True if successful, false if not
	 */
	public boolean addPermission(Player player, String permission)
	{
		if (perm == null)
			return false;

		return perm.playerAdd(player, permission);
	}

	/**
	 * Removes a permission from a player
	 * 
	 * @param player Player to remove permission from
	 * @param permission Permission to remove
	 * @return True if successful, false if not
	 */
	public boolean removePermission(Player player, String permission)
	{
		if (perm == null)
			return false;

		return perm.playerRemove(player, permission);
	}

	/**
	 * Gets a player's primary group.
	 * 
	 * @param player Player to get group for
	 * @return The group
	 */
	public String getGroup(Player player)
	{
		if (perm == null)
			return null;

		return perm.getPrimaryGroup(player);
	}

	// ---- Chat Methods

	public Chat getChat()
	{
		return chat;
	}

	public boolean isChatPresent()
	{
		return chat != null;
	}

	/**
	 * Gets a List of available groups.
	 * 
	 * @return The list
	 */
	public List<String> getGroups()
	{
		if (chat == null)
			return null;

		return ListUtil.toList(chat.getGroups());
	}

	/**
	 * Gets a player's prefix.
	 * 
	 * @param player Player to get prefix for
	 * @return The prefix
	 */
	public String getPrefix(Player player)
	{
		if (chat == null)
			return null;

		return chat.getPlayerPrefix(player);
	}

	/**
	 * Gets a player's suffix.
	 * 
	 * @param player Player to get suffix for
	 * @return The suffix
	 */
	public String getSuffix(Player player)
	{
		if (chat == null)
			return null;

		return chat.getPlayerSuffix(player);
	}

	/**
	 * Whether or not a player is in a group.
	 * 
	 * @param player Player to check
	 * @param group Group to check for
	 * @return True if they are, false if not.
	 */
	public boolean isPlayerInGroup(Player player, String group)
	{
		if (chat == null)
			return false;

		return chat.playerInGroup(player, group);
	}
}
