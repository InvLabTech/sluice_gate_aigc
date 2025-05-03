This is a Minecraft 1.20.1 Forge Mod, 99% by Deepseek, I just did the structuring.
Still Work in progress, not functioning for now.
这是一个Minecraft 1.20.1 Forge模组，计划可供生成闸门用作游戏装饰或建筑用途。
仍在开发中，目前功能不能工作。

AI prompt：

项目：
多方块动态闸门

特性：
任意矩形确认闸门
用闸门块填充区域后以工具右键确认闸门生成
开关过程动态
在大量存在以及开关动画情况下不会造成网络堵塞
门可以配置成{可直接开关、需要接受红石信号打开、需要接受红石信号关闭、保持开启、保持关闭、接受到红石信号改变开关状态、需要输入密码打开特定时间、需要输入密码切换状态、需要输入密码打开并自由关闭}等交互模式
存在门伺服方块，可配置代替门接收信号、密码或输出信号
密码简单在服务端对比哈希，不进行复杂处理
每扇门所属于某个team，team中的所有玩家可在非创造模式下使用工具物品修改闸门属性，创造模式则可以任意修改任何闸门
每扇门拥有独一无二的依次分配的gateID，可用于管理指令中指定处理目标闸门

约定：
使用命名空间"sluice_gate:"
包名"com.invlab.sluicegate"
命令:
/sluice_gate	//门配置命令，语法/sluice_gate gate_id get|set owner_team|password|speed（以及获取和修改其他的闸门属性）
/sg				// /sluice_gate的缩写形式

项目功能需要在文件中完整完成，不要使用Minecraft的Forge 1.20.1环境下不存在的内容，避免反复增加文件和内容。
你将按照我的要求向我教授各个功能的实现。
如何实现创建一个闸门整体、将这个闸门整体实体化并动态平移、调整它的碰撞箱？