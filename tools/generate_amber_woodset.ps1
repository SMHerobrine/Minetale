$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$resourcesRoot = Join-Path $root "src\main\resources"
$assetsRoot = Join-Path $resourcesRoot "assets\minetale"
$dataRoot = Join-Path $resourcesRoot "data"
$minecraftClientJar = Join-Path $env:USERPROFILE ".gradle\caches\fabric-loom\26.1.1\minecraft-client.jar"
$clientZip = [IO.Compression.ZipFile]::OpenRead($minecraftClientJar)

function Ensure-Dir([string] $path) {
	if (-not (Test-Path $path)) {
		New-Item -ItemType Directory -Path $path | Out-Null
	}
}

function Write-Text([string] $path, [string] $content) {
	Ensure-Dir ([IO.Path]::GetDirectoryName($path))
	Set-Content -Path $path -Value $content -Encoding utf8
}

function Get-ZipEntry([IO.Compression.ZipArchive] $zip, [string] $entryName) {
	$entry = $zip.Entries | Where-Object FullName -eq $entryName

	if (-not $entry) {
		throw "Missing zip entry: $entryName"
	}

	return $entry
}

function Get-ZipText([IO.Compression.ZipArchive] $zip, [string] $entryName) {
	$entry = Get-ZipEntry $zip $entryName
	$reader = New-Object IO.StreamReader($entry.Open())

	try {
		return $reader.ReadToEnd()
	} finally {
		$reader.Dispose()
	}
}

function Get-ZipBytes([IO.Compression.ZipArchive] $zip, [string] $entryName) {
	$entry = Get-ZipEntry $zip $entryName
	$stream = $entry.Open()

	try {
		$memory = New-Object IO.MemoryStream
		$stream.CopyTo($memory)
		return $memory.ToArray()
	} finally {
		$stream.Dispose()
	}
}

function Save-TintedPng([string] $entryName, [string] $destination) {
	$bytes = Get-ZipBytes $clientZip $entryName
	$memory = New-Object IO.MemoryStream(, $bytes)
	$sourceBitmap = [System.Drawing.Bitmap]::FromStream($memory)
	$bitmap = New-Object System.Drawing.Bitmap($sourceBitmap.Width, $sourceBitmap.Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
	$graphics = [System.Drawing.Graphics]::FromImage($bitmap)

	try {
		$graphics.DrawImage($sourceBitmap, 0, 0, $sourceBitmap.Width, $sourceBitmap.Height)

		for ($x = 0; $x -lt $bitmap.Width; $x++) {
			for ($y = 0; $y -lt $bitmap.Height; $y++) {
				$pixel = $bitmap.GetPixel($x, $y)

				if ($pixel.A -eq 0) {
					continue
				}

				$luma = (0.299 * $pixel.R + 0.587 * $pixel.G + 0.114 * $pixel.B) / 255.0
				$warmth = (($pixel.R - $pixel.B) / 255.0 + 1.0) / 2.0
				$shadow = [System.Drawing.Color]::FromArgb(255, 78, 42, 12)
				$mid = [System.Drawing.Color]::FromArgb(255, 177, 98, 24)
				$highlight = [System.Drawing.Color]::FromArgb(255, 247, 205, 112)

				if ($luma -lt 0.55) {
					$t = $luma / 0.55
					$r = [int] ($shadow.R + ($mid.R - $shadow.R) * $t)
					$g = [int] ($shadow.G + ($mid.G - $shadow.G) * $t)
					$b = [int] ($shadow.B + ($mid.B - $shadow.B) * $t)
				} else {
					$t = ($luma - 0.55) / 0.45
					$r = [int] ($mid.R + ($highlight.R - $mid.R) * $t)
					$g = [int] ($mid.G + ($highlight.G - $mid.G) * $t)
					$b = [int] ($mid.B + ($highlight.B - $mid.B) * $t)
				}

				$r = [Math]::Min(255, [int] ($r * (0.92 + $warmth * 0.18)))
				$g = [Math]::Min(255, [int] ($g * (0.95 + $warmth * 0.08)))

				$bitmap.SetPixel($x, $y, [System.Drawing.Color]::FromArgb($pixel.A, $r, $g, $b))
			}
		}

		Ensure-Dir ([IO.Path]::GetDirectoryName($destination))
		$bitmap.Save($destination, [System.Drawing.Imaging.ImageFormat]::Png)
	} finally {
		$graphics.Dispose()
		$sourceBitmap.Dispose()
		$bitmap.Dispose()
		$memory.Dispose()
	}
}

$blockstatesDir = Join-Path $assetsRoot "blockstates"
$blockModelsDir = Join-Path $assetsRoot "models\block"
$itemModelsDir = Join-Path $assetsRoot "models\item"
$blockTexturesDir = Join-Path $assetsRoot "textures\block"
$itemTexturesDir = Join-Path $assetsRoot "textures\item"
$entitySignsDir = Join-Path $assetsRoot "textures\entity\signs"
$entityHangingSignsDir = Join-Path $assetsRoot "textures\entity\signs\hanging"
$guiHangingSignsDir = Join-Path $assetsRoot "textures\gui\hanging_signs"
$langDir = Join-Path $assetsRoot "lang"

$lootDir = Join-Path $dataRoot "minetale\loot_table\blocks"
$recipeDir = Join-Path $dataRoot "minetale\recipe"
$amberBlockTagsDir = Join-Path $dataRoot "minetale\tags\block"
$amberItemTagsDir = Join-Path $dataRoot "minetale\tags\item"
$minecraftBlockTagsDir = Join-Path $dataRoot "minecraft\tags\block"
$minecraftItemTagsDir = Join-Path $dataRoot "minecraft\tags\item"
$axeMineableDir = Join-Path $minecraftBlockTagsDir "mineable"

$generatedFiles = @{
	(Join-Path $blockstatesDir "amber_log.json") = @'
{
  "variants": {
    "axis=x": {
      "model": "minetale:block/amber_log_horizontal",
      "x": 90,
      "y": 90
    },
    "axis=y": {
      "model": "minetale:block/amber_log"
    },
    "axis=z": {
      "model": "minetale:block/amber_log_horizontal",
      "x": 90
    }
  }
}
'@
	(Join-Path $blockstatesDir "amber_wood.json") = @'
{
  "variants": {
    "axis=x": {
      "model": "minetale:block/amber_wood",
      "x": 90,
      "y": 90
    },
    "axis=y": {
      "model": "minetale:block/amber_wood"
    },
    "axis=z": {
      "model": "minetale:block/amber_wood",
      "x": 90
    }
  }
}
'@
	(Join-Path $blockstatesDir "stripped_amber_log.json") = @'
{
  "variants": {
    "axis=x": {
      "model": "minetale:block/stripped_amber_log_horizontal",
      "x": 90,
      "y": 90
    },
    "axis=y": {
      "model": "minetale:block/stripped_amber_log"
    },
    "axis=z": {
      "model": "minetale:block/stripped_amber_log_horizontal",
      "x": 90
    }
  }
}
'@
	(Join-Path $blockstatesDir "stripped_amber_wood.json") = @'
{
  "variants": {
    "axis=x": {
      "model": "minetale:block/stripped_amber_wood",
      "x": 90,
      "y": 90
    },
    "axis=y": {
      "model": "minetale:block/stripped_amber_wood"
    },
    "axis=z": {
      "model": "minetale:block/stripped_amber_wood",
      "x": 90
    }
  }
}
'@
	(Join-Path $blockstatesDir "amber_planks.json") = '{"variants":{"":{"model":"minetale:block/amber_planks"}}}'
	(Join-Path $blockstatesDir "amber_stairs.json") = (Get-ZipText $clientZip "assets/minecraft/blockstates/oak_stairs.json").Replace("minecraft:block/oak_", "minetale:block/amber_")
	(Join-Path $blockstatesDir "amber_slab.json") = @'
{
  "variants": {
    "type=bottom": {
      "model": "minetale:block/amber_slab"
    },
    "type=double": {
      "model": "minetale:block/amber_planks"
    },
    "type=top": {
      "model": "minetale:block/amber_slab_top"
    }
  }
}
'@
	(Join-Path $blockstatesDir "amber_fence.json") = (Get-ZipText $clientZip "assets/minecraft/blockstates/oak_fence.json").Replace("minecraft:block/oak_", "minetale:block/amber_")
	(Join-Path $blockstatesDir "amber_fence_gate.json") = (Get-ZipText $clientZip "assets/minecraft/blockstates/oak_fence_gate.json").Replace("minecraft:block/oak_", "minetale:block/amber_")
	(Join-Path $blockstatesDir "amber_door.json") = (Get-ZipText $clientZip "assets/minecraft/blockstates/oak_door.json").Replace("minecraft:block/oak_", "minetale:block/amber_")
	(Join-Path $blockstatesDir "amber_trapdoor.json") = (Get-ZipText $clientZip "assets/minecraft/blockstates/oak_trapdoor.json").Replace("minecraft:block/oak_", "minetale:block/amber_")
	(Join-Path $blockstatesDir "amber_button.json") = (Get-ZipText $clientZip "assets/minecraft/blockstates/oak_button.json").Replace("minecraft:block/oak_", "minetale:block/amber_")
	(Join-Path $blockstatesDir "amber_pressure_plate.json") = @'
{
  "variants": {
    "powered=false": {
      "model": "minetale:block/amber_pressure_plate"
    },
    "powered=true": {
      "model": "minetale:block/amber_pressure_plate_down"
    }
  }
}
'@
	(Join-Path $blockstatesDir "amber_sign.json") = '{"variants":{"":{"model":"minetale:block/amber_sign"}}}'
	(Join-Path $blockstatesDir "amber_wall_sign.json") = '{"variants":{"":{"model":"minetale:block/amber_sign"}}}'
	(Join-Path $blockstatesDir "amber_hanging_sign.json") = '{"variants":{"":{"model":"minetale:block/amber_hanging_sign"}}}'
	(Join-Path $blockstatesDir "amber_wall_hanging_sign.json") = '{"variants":{"":{"model":"minetale:block/amber_hanging_sign"}}}'

	(Join-Path $blockModelsDir "amber_log.json") = '{"parent":"minecraft:block/cube_column","textures":{"end":"minetale:block/amber_log_top","side":"minetale:block/amber_log"}}'
	(Join-Path $blockModelsDir "amber_log_horizontal.json") = '{"parent":"minecraft:block/cube_column_horizontal","textures":{"end":"minetale:block/amber_log_top","side":"minetale:block/amber_log"}}'
	(Join-Path $blockModelsDir "amber_wood.json") = '{"parent":"minecraft:block/cube_column","textures":{"end":"minetale:block/amber_log","side":"minetale:block/amber_log"}}'
	(Join-Path $blockModelsDir "stripped_amber_log.json") = '{"parent":"minecraft:block/cube_column","textures":{"end":"minetale:block/stripped_amber_log_top","side":"minetale:block/stripped_amber_log"}}'
	(Join-Path $blockModelsDir "stripped_amber_log_horizontal.json") = '{"parent":"minecraft:block/cube_column_horizontal","textures":{"end":"minetale:block/stripped_amber_log_top","side":"minetale:block/stripped_amber_log"}}'
	(Join-Path $blockModelsDir "stripped_amber_wood.json") = '{"parent":"minecraft:block/cube_column","textures":{"end":"minetale:block/stripped_amber_log","side":"minetale:block/stripped_amber_log"}}'
	(Join-Path $blockModelsDir "amber_planks.json") = '{"parent":"minecraft:block/cube_all","textures":{"all":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_stairs.json") = '{"parent":"minecraft:block/stairs","textures":{"bottom":"minetale:block/amber_planks","side":"minetale:block/amber_planks","top":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_stairs_inner.json") = '{"parent":"minecraft:block/inner_stairs","textures":{"bottom":"minetale:block/amber_planks","side":"minetale:block/amber_planks","top":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_stairs_outer.json") = '{"parent":"minecraft:block/outer_stairs","textures":{"bottom":"minetale:block/amber_planks","side":"minetale:block/amber_planks","top":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_slab.json") = '{"parent":"minecraft:block/slab","textures":{"bottom":"minetale:block/amber_planks","side":"minetale:block/amber_planks","top":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_slab_top.json") = '{"parent":"minecraft:block/slab_top","textures":{"bottom":"minetale:block/amber_planks","side":"minetale:block/amber_planks","top":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_fence_post.json") = '{"parent":"minecraft:block/fence_post","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_fence_side.json") = '{"parent":"minecraft:block/fence_side","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_fence_inventory.json") = '{"parent":"minecraft:block/fence_inventory","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_fence_gate.json") = '{"parent":"minecraft:block/template_fence_gate","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_fence_gate_open.json") = '{"parent":"minecraft:block/template_fence_gate_open","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_fence_gate_wall.json") = '{"parent":"minecraft:block/template_fence_gate_wall","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_fence_gate_wall_open.json") = '{"parent":"minecraft:block/template_fence_gate_wall_open","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_door_bottom_left.json") = '{"parent":"minecraft:block/door_bottom_left","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_door_bottom_left_open.json") = '{"parent":"minecraft:block/door_bottom_left_open","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_door_bottom_right.json") = '{"parent":"minecraft:block/door_bottom_right","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_door_bottom_right_open.json") = '{"parent":"minecraft:block/door_bottom_right_open","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_door_top_left.json") = '{"parent":"minecraft:block/door_top_left","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_door_top_left_open.json") = '{"parent":"minecraft:block/door_top_left_open","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_door_top_right.json") = '{"parent":"minecraft:block/door_top_right","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_door_top_right_open.json") = '{"parent":"minecraft:block/door_top_right_open","textures":{"bottom":"minetale:block/amber_door_bottom","top":"minetale:block/amber_door_top"}}'
	(Join-Path $blockModelsDir "amber_trapdoor_bottom.json") = '{"parent":"minecraft:block/template_trapdoor_bottom","textures":{"texture":"minetale:block/amber_trapdoor"}}'
	(Join-Path $blockModelsDir "amber_trapdoor_open.json") = '{"parent":"minecraft:block/template_trapdoor_open","textures":{"texture":"minetale:block/amber_trapdoor"}}'
	(Join-Path $blockModelsDir "amber_trapdoor_top.json") = '{"parent":"minecraft:block/template_trapdoor_top","textures":{"texture":"minetale:block/amber_trapdoor"}}'
	(Join-Path $blockModelsDir "amber_button.json") = '{"parent":"minecraft:block/button","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_button_pressed.json") = '{"parent":"minecraft:block/button_pressed","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_button_inventory.json") = '{"parent":"minecraft:block/button_inventory","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_pressure_plate.json") = '{"parent":"minecraft:block/pressure_plate_up","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_pressure_plate_down.json") = '{"parent":"minecraft:block/pressure_plate_down","textures":{"texture":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_sign.json") = '{"textures":{"particle":"minetale:block/amber_planks"}}'
	(Join-Path $blockModelsDir "amber_hanging_sign.json") = '{"textures":{"particle":"minetale:block/stripped_amber_log"}}'

	(Join-Path $itemModelsDir "amber_log.json") = '{"parent":"minetale:block/amber_log"}'
	(Join-Path $itemModelsDir "amber_wood.json") = '{"parent":"minetale:block/amber_wood"}'
	(Join-Path $itemModelsDir "stripped_amber_log.json") = '{"parent":"minetale:block/stripped_amber_log"}'
	(Join-Path $itemModelsDir "stripped_amber_wood.json") = '{"parent":"minetale:block/stripped_amber_wood"}'
	(Join-Path $itemModelsDir "amber_planks.json") = '{"parent":"minetale:block/amber_planks"}'
	(Join-Path $itemModelsDir "amber_stairs.json") = '{"parent":"minetale:block/amber_stairs"}'
	(Join-Path $itemModelsDir "amber_slab.json") = '{"parent":"minetale:block/amber_slab"}'
	(Join-Path $itemModelsDir "amber_fence.json") = '{"parent":"minetale:block/amber_fence_inventory"}'
	(Join-Path $itemModelsDir "amber_fence_gate.json") = '{"parent":"minetale:block/amber_fence_gate"}'
	(Join-Path $itemModelsDir "amber_trapdoor.json") = '{"parent":"minetale:block/amber_trapdoor_bottom"}'
	(Join-Path $itemModelsDir "amber_button.json") = '{"parent":"minetale:block/amber_button_inventory"}'
	(Join-Path $itemModelsDir "amber_pressure_plate.json") = '{"parent":"minetale:block/amber_pressure_plate"}'
	(Join-Path $itemModelsDir "amber_door.json") = '{"parent":"minecraft:item/generated","textures":{"layer0":"minetale:item/amber_door"}}'
	(Join-Path $itemModelsDir "amber_sign.json") = '{"parent":"minecraft:item/generated","textures":{"layer0":"minetale:item/amber_sign"}}'
	(Join-Path $itemModelsDir "amber_hanging_sign.json") = '{"parent":"minecraft:item/generated","textures":{"layer0":"minetale:item/amber_hanging_sign"}}'

	(Join-Path $recipeDir "amber_planks.json") = '{"type":"minecraft:crafting_shapeless","category":"building","group":"planks","ingredients":["#minetale:amber_logs"],"result":{"count":4,"id":"minetale:amber_planks"}}'
	(Join-Path $recipeDir "amber_stairs.json") = '{"type":"minecraft:crafting_shaped","category":"building","group":"wooden_stairs","key":{"#":"minetale:amber_planks"},"pattern":["#  ","## ","###"],"result":{"count":4,"id":"minetale:amber_stairs"}}'
	(Join-Path $recipeDir "amber_slab.json") = '{"type":"minecraft:crafting_shaped","category":"building","group":"wooden_slab","key":{"#":"minetale:amber_planks"},"pattern":["###"],"result":{"count":6,"id":"minetale:amber_slab"}}'
	(Join-Path $recipeDir "amber_fence.json") = '{"type":"minecraft:crafting_shaped","category":"misc","group":"wooden_fence","key":{"#":"minecraft:stick","W":"minetale:amber_planks"},"pattern":["W#W","W#W"],"result":{"count":3,"id":"minetale:amber_fence"}}'
	(Join-Path $recipeDir "amber_fence_gate.json") = '{"type":"minecraft:crafting_shaped","category":"redstone","group":"wooden_fence_gate","key":{"#":"minecraft:stick","W":"minetale:amber_planks"},"pattern":["#W#","#W#"],"result":{"id":"minetale:amber_fence_gate"}}'
	(Join-Path $recipeDir "amber_door.json") = '{"type":"minecraft:crafting_shaped","category":"redstone","group":"wooden_door","key":{"#":"minetale:amber_planks"},"pattern":["##","##","##"],"result":{"count":3,"id":"minetale:amber_door"}}'
	(Join-Path $recipeDir "amber_trapdoor.json") = '{"type":"minecraft:crafting_shaped","category":"redstone","group":"wooden_trapdoor","key":{"#":"minetale:amber_planks"},"pattern":["###","###"],"result":{"count":2,"id":"minetale:amber_trapdoor"}}'
	(Join-Path $recipeDir "amber_button.json") = '{"type":"minecraft:crafting_shapeless","category":"redstone","group":"wooden_button","ingredients":["minetale:amber_planks"],"result":{"id":"minetale:amber_button"}}'
	(Join-Path $recipeDir "amber_pressure_plate.json") = '{"type":"minecraft:crafting_shaped","category":"redstone","group":"wooden_pressure_plate","key":{"#":"minetale:amber_planks"},"pattern":["##"],"result":{"id":"minetale:amber_pressure_plate"}}'
	(Join-Path $recipeDir "amber_sign.json") = '{"type":"minecraft:crafting_shaped","category":"misc","group":"wooden_sign","key":{"#":"minetale:amber_planks","X":"minecraft:stick"},"pattern":["###","###"," X "],"result":{"count":3,"id":"minetale:amber_sign"}}'
	(Join-Path $recipeDir "amber_hanging_sign.json") = '{"type":"minecraft:crafting_shaped","category":"misc","group":"hanging_sign","key":{"#":"minetale:stripped_amber_log","X":"minecraft:iron_chain"},"pattern":["X X","###","###"],"result":{"count":6,"id":"minetale:amber_hanging_sign"}}'
	(Join-Path $recipeDir "amber_wood.json") = '{"type":"minecraft:crafting_shaped","category":"building","group":"bark","key":{"#":"minetale:amber_log"},"pattern":["##","##"],"result":{"count":3,"id":"minetale:amber_wood"}}'
	(Join-Path $recipeDir "stripped_amber_wood.json") = '{"type":"minecraft:crafting_shaped","category":"building","group":"bark","key":{"#":"minetale:stripped_amber_log"},"pattern":["##","##"],"result":{"count":3,"id":"minetale:stripped_amber_wood"}}'

	(Join-Path $lootDir "amber_slab.json") = '{"type":"minecraft:block","pools":[{"bonus_rolls":0.0,"entries":[{"type":"minecraft:item","functions":[{"add":false,"conditions":[{"block":"minetale:amber_slab","condition":"minecraft:block_state_property","properties":{"type":"double"}}],"count":2.0,"function":"minecraft:set_count"},{"function":"minecraft:explosion_decay"}],"name":"minetale:amber_slab"}],"rolls":1.0}],"random_sequence":"minetale:blocks/amber_slab"}'
	(Join-Path $lootDir "amber_door.json") = '{"type":"minecraft:block","pools":[{"bonus_rolls":0.0,"conditions":[{"condition":"minecraft:survives_explosion"}],"entries":[{"type":"minecraft:item","conditions":[{"block":"minetale:amber_door","condition":"minecraft:block_state_property","properties":{"half":"lower"}}],"name":"minetale:amber_door"}],"rolls":1.0}],"random_sequence":"minetale:blocks/amber_door"}'
	(Join-Path $amberBlockTagsDir "amber_logs.json") = '{"values":["minetale:amber_log","minetale:amber_wood","minetale:stripped_amber_log","minetale:stripped_amber_wood"]}'
	(Join-Path $amberItemTagsDir "amber_logs.json") = '{"values":["minetale:amber_log","minetale:amber_wood","minetale:stripped_amber_log","minetale:stripped_amber_wood"]}'
	(Join-Path $minecraftBlockTagsDir "logs_that_burn.json") = '{"replace":false,"values":["#minetale:amber_logs"]}'
	(Join-Path $minecraftBlockTagsDir "planks.json") = '{"replace":false,"values":["minetale:amber_planks"]}'
	(Join-Path $minecraftBlockTagsDir "wooden_stairs.json") = '{"replace":false,"values":["minetale:amber_stairs"]}'
	(Join-Path $minecraftBlockTagsDir "wooden_slabs.json") = '{"replace":false,"values":["minetale:amber_slab"]}'
	(Join-Path $minecraftBlockTagsDir "wooden_fences.json") = '{"replace":false,"values":["minetale:amber_fence"]}'
	(Join-Path $minecraftBlockTagsDir "fences.json") = '{"replace":false,"values":["minetale:amber_fence"]}'
	(Join-Path $minecraftBlockTagsDir "fence_gates.json") = '{"replace":false,"values":["minetale:amber_fence_gate"]}'
	(Join-Path $minecraftBlockTagsDir "wooden_doors.json") = '{"replace":false,"values":["minetale:amber_door"]}'
	(Join-Path $minecraftBlockTagsDir "wooden_trapdoors.json") = '{"replace":false,"values":["minetale:amber_trapdoor"]}'
	(Join-Path $minecraftBlockTagsDir "wooden_buttons.json") = '{"replace":false,"values":["minetale:amber_button"]}'
	(Join-Path $minecraftBlockTagsDir "wooden_pressure_plates.json") = '{"replace":false,"values":["minetale:amber_pressure_plate"]}'
	(Join-Path $minecraftBlockTagsDir "standing_signs.json") = '{"replace":false,"values":["minetale:amber_sign"]}'
	(Join-Path $minecraftBlockTagsDir "wall_signs.json") = '{"replace":false,"values":["minetale:amber_wall_sign"]}'
	(Join-Path $minecraftBlockTagsDir "ceiling_hanging_signs.json") = '{"replace":false,"values":["minetale:amber_hanging_sign"]}'
	(Join-Path $minecraftBlockTagsDir "wall_hanging_signs.json") = '{"replace":false,"values":["minetale:amber_wall_hanging_sign"]}'
	(Join-Path $axeMineableDir "axe.json") = '{"replace":false,"values":["minetale:amber_log","minetale:amber_wood","minetale:stripped_amber_log","minetale:stripped_amber_wood","minetale:amber_planks","minetale:amber_stairs","minetale:amber_slab","minetale:amber_fence","minetale:amber_fence_gate","minetale:amber_door","minetale:amber_trapdoor","minetale:amber_button","minetale:amber_pressure_plate","minetale:amber_sign","minetale:amber_wall_sign","minetale:amber_hanging_sign","minetale:amber_wall_hanging_sign"]}'
	(Join-Path $minecraftItemTagsDir "logs_that_burn.json") = '{"replace":false,"values":["#minetale:amber_logs"]}'
	(Join-Path $minecraftItemTagsDir "planks.json") = '{"replace":false,"values":["minetale:amber_planks"]}'
	(Join-Path $minecraftItemTagsDir "wooden_stairs.json") = '{"replace":false,"values":["minetale:amber_stairs"]}'
	(Join-Path $minecraftItemTagsDir "wooden_slabs.json") = '{"replace":false,"values":["minetale:amber_slab"]}'
	(Join-Path $minecraftItemTagsDir "wooden_fences.json") = '{"replace":false,"values":["minetale:amber_fence"]}'
	(Join-Path $minecraftItemTagsDir "fences.json") = '{"replace":false,"values":["minetale:amber_fence"]}'
	(Join-Path $minecraftItemTagsDir "fence_gates.json") = '{"replace":false,"values":["minetale:amber_fence_gate"]}'
	(Join-Path $minecraftItemTagsDir "wooden_doors.json") = '{"replace":false,"values":["minetale:amber_door"]}'
	(Join-Path $minecraftItemTagsDir "wooden_trapdoors.json") = '{"replace":false,"values":["minetale:amber_trapdoor"]}'
	(Join-Path $minecraftItemTagsDir "wooden_buttons.json") = '{"replace":false,"values":["minetale:amber_button"]}'
	(Join-Path $minecraftItemTagsDir "wooden_pressure_plates.json") = '{"replace":false,"values":["minetale:amber_pressure_plate"]}'
	(Join-Path $minecraftItemTagsDir "signs.json") = '{"replace":false,"values":["minetale:amber_sign"]}'
	(Join-Path $minecraftItemTagsDir "hanging_signs.json") = '{"replace":false,"values":["minetale:amber_hanging_sign"]}'
	(Join-Path $langDir "en_us.json") = @'
{
  "block.minetale.amber_log": "Amber Log",
  "block.minetale.amber_wood": "Amber Wood",
  "block.minetale.stripped_amber_log": "Stripped Amber Log",
  "block.minetale.stripped_amber_wood": "Stripped Amber Wood",
  "block.minetale.amber_planks": "Amber Planks",
  "block.minetale.amber_stairs": "Amber Stairs",
  "block.minetale.amber_slab": "Amber Slab",
  "block.minetale.amber_fence": "Amber Fence",
  "block.minetale.amber_fence_gate": "Amber Fence Gate",
  "block.minetale.amber_door": "Amber Door",
  "block.minetale.amber_trapdoor": "Amber Trapdoor",
  "block.minetale.amber_button": "Amber Button",
  "block.minetale.amber_pressure_plate": "Amber Pressure Plate",
  "block.minetale.amber_sign": "Amber Sign",
  "block.minetale.amber_hanging_sign": "Amber Hanging Sign",
  "item.minetale.amber_sign": "Amber Sign",
  "item.minetale.amber_hanging_sign": "Amber Hanging Sign"
}
'@
}

foreach ($entry in $generatedFiles.GetEnumerator()) {
	Write-Text $entry.Key $entry.Value
}

foreach ($name in @("amber_log", "amber_wood", "stripped_amber_log", "stripped_amber_wood", "amber_planks", "amber_stairs", "amber_fence", "amber_fence_gate", "amber_trapdoor", "amber_button", "amber_pressure_plate", "amber_sign", "amber_hanging_sign")) {
	Write-Text (Join-Path $lootDir "$name.json") ("{`"type`":`"minecraft:block`",`"pools`":[{`"bonus_rolls`":0.0,`"conditions`":[{`"condition`":`"minecraft:survives_explosion`"}],`"entries`":[{`"type`":`"minecraft:item`",`"name`":`"minetale:$name`"}],`"rolls`":1.0}],`"random_sequence`":`"minetale:blocks/$name`"}")
}

foreach ($texture in @(
	@{ Source = "assets/minecraft/textures/block/oak_log.png"; Destination = Join-Path $blockTexturesDir "amber_log.png" },
	@{ Source = "assets/minecraft/textures/block/oak_log_top.png"; Destination = Join-Path $blockTexturesDir "amber_log_top.png" },
	@{ Source = "assets/minecraft/textures/block/stripped_oak_log.png"; Destination = Join-Path $blockTexturesDir "stripped_amber_log.png" },
	@{ Source = "assets/minecraft/textures/block/stripped_oak_log_top.png"; Destination = Join-Path $blockTexturesDir "stripped_amber_log_top.png" },
	@{ Source = "assets/minecraft/textures/block/oak_planks.png"; Destination = Join-Path $blockTexturesDir "amber_planks.png" },
	@{ Source = "assets/minecraft/textures/block/oak_door_bottom.png"; Destination = Join-Path $blockTexturesDir "amber_door_bottom.png" },
	@{ Source = "assets/minecraft/textures/block/oak_door_top.png"; Destination = Join-Path $blockTexturesDir "amber_door_top.png" },
	@{ Source = "assets/minecraft/textures/block/oak_trapdoor.png"; Destination = Join-Path $blockTexturesDir "amber_trapdoor.png" },
	@{ Source = "assets/minecraft/textures/item/oak_door.png"; Destination = Join-Path $itemTexturesDir "amber_door.png" },
	@{ Source = "assets/minecraft/textures/item/oak_sign.png"; Destination = Join-Path $itemTexturesDir "amber_sign.png" },
	@{ Source = "assets/minecraft/textures/item/oak_hanging_sign.png"; Destination = Join-Path $itemTexturesDir "amber_hanging_sign.png" },
	@{ Source = "assets/minecraft/textures/entity/signs/oak.png"; Destination = Join-Path $entitySignsDir "amber.png" },
	@{ Source = "assets/minecraft/textures/entity/signs/hanging/oak.png"; Destination = Join-Path $entityHangingSignsDir "amber.png" },
	@{ Source = "assets/minecraft/textures/gui/hanging_signs/oak.png"; Destination = Join-Path $guiHangingSignsDir "amber.png" }
)) {
	Save-TintedPng $texture.Source $texture.Destination
}

$clientZip.Dispose()
