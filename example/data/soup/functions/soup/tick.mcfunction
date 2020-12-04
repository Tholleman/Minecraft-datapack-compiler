effect give @a[scores={soup=1..}] minecraft:saturation 1 10 true
scoreboard players set @a[scores={soup=1..}] soup 0
execute if entity @p[nbt={SelectedItem:{id:"minecraft:rabbit_stew"}}] run replaceitem entity @p weapon.mainhand rabbit_stew{display:{Name:'{"text":"Soup","italic":false}'}} 1