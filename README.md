# Refraction Continuation

A fork of the mod Refraction - A light manipulation based mod for The Modding Trials

https://www.curseforge.com/minecraft/mc-mods/refraction-continuation

> This version of Refraction is an unofficial port developed by HauntedPasta1 and based on the original work by Team Wizardry. This port has been created with our explicit permission.
>
> Please note that as it is an unofficial port, we cannot ensure the same level of quality as provided by Team Wizardry's original discontinued versions of this mod. Thank you for your support and understanding.
>
> Sincerely, Team Wizardry

CraftTweaker 2 Support:

mods.refraction.AssemblyTable.addRecipe(name, output, inputs, minLaserStrenght, maxLaserStrength, minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue)

mods.refraction.AssemblyTable.remove(output)

Examples:

This will create a recipe for the white creative filter, requiring 3 lenses + 3 bonemeal with a high strength orange beam (>128 strength, full red, half green, and no blue)

mods.refraction.AssemblyTable.addRecipe("name", <refraction:filter:0>, [<refraction:lens>, <refraction:lens>, <refraction:lens>, <minecraft:dye:15>, <minecraft:dye:15>, <minecraft:dye:15>], 128, 255, 255, 255, 96, 160, 0, 0)

This will remove the recipe for the prism

mods.refraction.AssemblyTable.remove(<refraction:prism>)
