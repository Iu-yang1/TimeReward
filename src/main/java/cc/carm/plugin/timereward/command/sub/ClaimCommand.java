package cc.carm.plugin.timereward.command.sub;

import cc.carm.lib.easyplugin.command.SubCommand;
import cc.carm.plugin.timereward.TimeRewardAPI;
import cc.carm.plugin.timereward.command.MainCommand;
import cc.carm.plugin.timereward.conf.PluginMessages;
import cc.carm.plugin.timereward.data.RewardContents;
import cc.carm.plugin.timereward.manager.RewardManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClaimCommand extends SubCommand<MainCommand> {

    public ClaimCommand(@NotNull MainCommand parent, String identifier, String... aliases) {
        super(parent, identifier, aliases);
    }

    @Override
    public Void execute(JavaPlugin plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            PluginMessages.NOT_PLAYER.sendTo(sender);
            return null;
        }

        Player player = (Player) sender;
        RewardManager manager = TimeRewardAPI.getRewardManager();

        @Nullable String rewardID = args.length > 0 ? args[0] : null;
        if (rewardID == null) {

            List<RewardContents> unclaimedRewards = manager.getUnclaimedRewards(player);
            if (unclaimedRewards.isEmpty()) {
                PluginMessages.NO_UNCLAIMED_REWARD.sendTo(sender);
                return null;
            }

            // 为玩家发放奖励
            manager.claimRewards(player, unclaimedRewards, false);
        } else {

            RewardContents reward = manager.getReward(rewardID);
            if (reward == null) {
                PluginMessages.NOT_EXISTS.sendTo(sender, rewardID);
                return null;
            }

            if (!manager.isClaimable(player, reward)) {
                PluginMessages.NOT_CLAIMABLE.sendTo(sender, reward.getDisplayName());
                return null;
            }

            // 为玩家发放奖励
            manager.claimReward(player, reward, false);
        }
        return null;
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("TimeReward.claim");
    }
    
}
