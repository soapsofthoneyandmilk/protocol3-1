package protocol3.events;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import protocol3.backend.Config;
import protocol3.backend.ItemCheck;
import protocol3.backend.PlayerMeta;

import java.util.Random;

public class ItemCheckTriggers implements Listener
{

	static Material[] lagItems = { Material.REDSTONE, Material.REDSTONE_BLOCK, Material.ARMOR_STAND,
			Material.STICKY_PISTON, Material.PISTON, Material.REDSTONE_WALL_TORCH, Material.COMPARATOR,
			Material.REDSTONE_WIRE, Material.REPEATER, Material.OBSERVER, Material.LEVER };

	static Random r = new Random();

	@EventHandler
	public void onPlace(BlockPlaceEvent e)
	{
		// Exempt ender portal frames; they are illegal but this event
		// gets
		// triggered when player adds eye of ender to portal to fire it.
		if (e.getItemInHand().getType().equals(Material.ENDER_EYE))
			return;

		if (PlayerMeta.isLagfag(e.getPlayer()))
		{
			for (Material m : lagItems)
			{
				if (e.getBlock().getType().equals(m))
				{
					e.setCancelled(true);
				}
			}

			int randomNumber = r.nextInt(9);

			if (randomNumber == 5 || randomNumber == 6)
			{
				e.getPlayer().spigot().sendMessage(new TextComponent("§cThis is what you get for being a lagfag!"));
				e.setCancelled(true);
				return;
			}
		}

		if (Config.getValue("place.illegal").equals("1"))
		{
			if (Config.getValue("place.illegal.ops").equals("0") && e.getPlayer().isOp())
			{
				return;
			}
			// Check if item isn't placeable
			for (Material m : ItemCheck.Banned)
			{
				if (e.getBlock().getType().equals(m))
				{
					e.setCancelled(true);
					if (Config.getValue("item.illegal.agro").equals("1"))
					{
						for (ItemStack is : e.getPlayer().getInventory())
						{
							ItemCheck.IllegalCheck(is);
						}
					}
				}
			}
		}

		// Check if item is illegal
		ItemCheck.IllegalCheck(e.getItemInHand());
	}

	@EventHandler
	public void onDispense(BlockDispenseArmorEvent e) {
		ItemCheck.IllegalCheck(e.getItem());
	}

	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event)
	{
		for (ItemStack i : event.getInventory().getStorageContents())
		{
			ItemCheck.IllegalCheck(i);
		}
	}

	// Prevents hopper exploits.
	@EventHandler
	public void onInventoryMovedItem(InventoryMoveItemEvent event)
	{
		if (Config.getValue("item.illegal.agro").equals("1"))
		{
			// ItemCheck.IllegalCheck(event.getItem());
			// for (ItemStack i : event.getSource())
			// ItemCheck.IllegalCheck(i);
		}
	}

	@EventHandler
	public void onPickupItem(EntityPickupItemEvent e)
	{
		if (e.getEntityType().equals(EntityType.PLAYER))
		{
			Player player = (Player) e.getEntity();
		}
		if (Config.getValue("item.illegal.agro").equals("1"))
		{
			ItemCheck.IllegalCheck(e.getItem().getItemStack());
			if (e.getEntityType().equals(EntityType.PLAYER))
			{
				Player player = (Player) e.getEntity();
				for (ItemStack is : player.getInventory())
				{
					ItemCheck.IllegalCheck(is);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (Config.getValue("item.illegal.agro").equals("1"))
		{
			ItemCheck.IllegalCheck(e.getCurrentItem());
		}
	}

}
