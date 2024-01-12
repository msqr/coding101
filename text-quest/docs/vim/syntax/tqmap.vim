" Text Quest Map syntax

if version < 600
  syntax clear
elseif exists("b:current_syntax")
  finish
endif

syn case match

syn keyword tqTodo		contained TODO FIXME XXX
syn cluster tqCommentGrp	contains=tqTodo

syn match tqCaveComment		/\<O\>/
syn match tqForestComment	/\s\^\s/hs=s+1,he=e-1
syn match tqGrassComment	/\s\.\s/hs=s+1,he=e-1
syn match tqHillComment		/\sm\s/hs=s+1,he=e-1
syn match tqLavaComment		/\s[=]\s/hs=s+1,he=e-1
syn match tqLavaRockComment	/\s["]\s/hs=s+1,he=e-1
syn match tqMountainComment 	/\<A\>/
syn match tqTownComment		/\s\*\s/hs=s+1,he=e-1
syn match tqSandComment		/\s,\s/hs=s+1,he=e-1
syn match tqShipComment		/\s[&]\s/hs=s+1,he=e-1
syn match tqShopComment		/\s[$]\s/hs=s+1,he=e-1
syn match tqWaterComment	/\s\~\s/hs=s+1,he=e-1

syn match tqCave	/O/
syn match tqForest	/\^/
syn match tqGrass	/\./
syn match tqHill	/m/
syn match tqLava	/[=]/
syn match tqLavaRock	/["]/
syn match tqMountain 	/A/
syn match tqSand	/,/
syn match tqShip	/[&]/
syn match tqShop	/[$]/
syn match tqTown	/\*/
syn match tqWater	/\~/

syn region tqComment start="^#" skip="\\$" end="$" keepend contains=@tqCommentGrp,@Spell,tqCaveComment,tqForestComment,tqGrassComment,tqLavaComment,tqLavaRockComment,tqHillComment,tqMountainComment,tqSandComment,tqShipComment,tqShopComment,tqTownComment,tqWaterComment

if version >= 508 || !exists("did_proto_syn_inits")
  if version < 508
    let did_proto_syn_inits = 1
    command -nargs=+ HiLink hi link <args>
  else
    command -nargs=+ HiLink hi def link <args>
  endif

  HiLink tqCave			Cave
  HiLink tqCaveComment		Cave
  HiLink tqForest		Forest
  HiLink tqForestComment	Forest
  HiLink tqGrass		Grass
  HiLink tqGrassComment		Grass
  HiLink tqLava			Lava
  HiLink tqLavaComment		Lava
  HiLink tqLavaRock		LavaRock
  HiLink tqLavaRockComment	LavaRock
  HiLink tqHill			Hill
  HiLink tqHillComment		Hill
  HiLink tqMountain		Mountain
  HiLink tqMountainComment	Mountain
  HiLink tqSand			Sand
  HiLink tqSandComment		Sand
  HiLink tqShip			Ship
  HiLink tqShipComment		Ship
  HiLink tqShop			Shop
  HiLink tqShopComment		Shop
  HiLink tqTown			Town
  HiLink tqTownComment		Town
  HiLink tqWater		Water
  HiLink tqWaterComment		Water

  HiLink tqComment      Comment

  delcommand HiLink
endif

let b:current_syntax = "tqmap"
