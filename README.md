<center><img src="https://i.postimg.cc/MKPVVR1s/dplogo-512.png" alt="logo"></center>
<center><img src="https://i.postimg.cc/RZ9dqPFx/introduce.png" alt="introduce"></center>

Example Video : *Coming soon!*

This plugin allows you to create special reward boxes on a Minecraft server that players can open to receive random or chosen items.  
Delight your players with exciting **mystery boxes** for events or giveaways, all configured easily without any complicated setup or configuration!

---

<center><img src="https://i.postimg.cc/RZ9dqP08/description.png" alt="description"></center>

- Create unlimited **Consume Boxes** with fully customizable reward items and prizes  
- Supports three box modes: **Random**, **Select**, and **Gift**  
- Configure **weighted random** chances for items in Random mode  
- Manage box contents and settings through an intuitive in-game **GUI**  
- Players open boxes using special **coupon items**

---

<center><img src="https://i.postimg.cc/rwcjzhpH/depend-plugin.png" alt="depend-plugin"></center>

- All DP-Plugins require the **`DPP-Core`** plugin  
- The plugin will not work if **`DPP-Core`** is not installed  
- You can download **`DPP-Core`** here: <a href="https://github.com/DP-Plugins/DPP-Core/releases" target="_blank">Click me!</a>  
- This plugin does **not** require additional plugins (PlaceholderAPI not required)

---

<center><img src="https://i.postimg.cc/dV01RxJB/installation.png" alt="installation"></center>

1️⃣ Place the **`DPP-Core`** plugin and this plugin file (**`DP-ConsumeBox-*.jar`**) into your server’s **`plugins`** folder  

2️⃣ Restart the server, and the plugin will be automatically enabled  

3️⃣ If needed, you can open and modify **`config.yml`** and **`plugin.yml`** to customize settings  

---

<center><img src="https://i.postimg.cc/jSKcC85K/settings.png" alt="settings"></center>

- **`config.yml`**: Manages basic plugin settings (prefix, language, etc.)

---

<center><img src="https://i.postimg.cc/SxqdjZKw/command.png" alt="command"></center>

❗ Some commands require admin permission (`dpcb.admin`)

**Command List and Examples**

| Command | Permission | Description | Example |
|-------|------------|-------------|---------|
| `/dpcb create <name> <type>` | dpcb.admin | Create a new Consume Box | `/dpcb create TreasureBox RANDOM` |
| `/dpcb items <name>` | dpcb.admin | Edit box items via GUI | `/dpcb items TreasureBox` |
| `/dpcb maxpage <name> <maxPage>` | dpcb.admin | Set maximum pages for a box | `/dpcb maxpage TreasureBox 3` |
| `/dpcb setSelectedItem` | dpcb.admin | Set selected-item marker | `/dpcb setSelectedItem` |
| `/dpcb setDefaultCoupon` | dpcb.admin | Set default coupon item | `/dpcb setDefaultCoupon` |
| `/dpcb setCoupon <name>` | dpcb.admin | Set custom coupon for a box | `/dpcb setCoupon TreasureBox` |
| `/dpcb remove <name>` | dpcb.admin | Remove a Consume Box | `/dpcb remove TreasureBox` |
| `/dpcb type <name> <type>` | dpcb.admin | Change box type | `/dpcb type TreasureBox GIFT` |
| `/dpcb randomType <name> <randomType>` | dpcb.admin | Set random mode (SIMPLE/WEIGHTED) | `/dpcb randomType TreasureBox WEIGHTED` |
| `/dpcb weight <name>` | dpcb.admin | Edit item weights via GUI | `/dpcb weight TreasureBox` |
| `/dpcb rewardAmount <name> <amount>` | dpcb.admin | Set reward amount | `/dpcb rewardAmount TreasureBox 2` |
| `/dpcb giveCoupon <name> <player> <amount>` | dpcb.admin | Give coupons to player | `/dpcb giveCoupon TreasureBox Steve 1` |
| `/dpcb list` | dpcb.admin | List all boxes | `/dpcb list` |
| `/dpcb reload` | dpcb.admin | Reload plugin data | `/dpcb reload` |

**❗Notes when using commands**

- Box names support Korean and English, but **spaces are not allowed**  
- Invalid parameters will display an error message  
- All GUI edits are **saved automatically**  
- Weighted mode requires assigning weights to each item  
- Admin commands require **OP** or `dpcb.admin` permission  

---

<center><img src="https://i.postimg.cc/Z5ZH0fqL/api-integration.png" alt="api-integration"></center>

- This plugin does **not** provide any api-integration.

---

<center><a href="https://discord.gg/JnMCqkn2FX"><img src="https://i.postimg.cc/4xZPn8dC/discord.png" alt="discord"></a></center>

- https://discord.gg/JnMCqkn2FX  
- For questions, bug reports, or feature suggestions, please join our Discord  
- Feedback and improvement ideas are always welcome!

---
