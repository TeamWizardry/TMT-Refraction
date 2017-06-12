# Refraction

[![Build Status](https://travis-ci.org/TeamWizardry/TMT-Refraction.svg?branch=master)](https://travis-ci.org/TeamWizardry/TMT-Refraction)

A light manipulation based mod for The Modding Trials

https://minecraft.curseforge.com/projects/refraction

Minetweaker Support:

mods.refraction.AssemblyTable.addRecipe(name, output, inputs, minLaserStrenght, maxLaserStrength, minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue)

mods.refraction.AssemblyTable.remove(output)

Examples:

This will create a recipe for the white creative filter, requiring 3 lenses + 3 bonemeal with a high strength orange beam (>128 strength, full red, half green, and no blue)

mods.refraction.AssemblyTable.addRecipe("name", <refraction:filter:0>, [<refraction:lens>, <refraction:lens>, <refraction:lens>, <minecraft:dye:15>, <minecraft:dye:15>, <minecraft:dye:15>], 128, 255, 255, 255, 96, 160, 0, 0)

This will remove the recipe for the prism

mods.refraction.AssemblyTable.remove(<refraction:prism>)
