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
syn match tqLavaComment		/\s[\=]\s/hs=s+1,he=e-1
syn match tqMountainComment 	/\<A\>/
syn match tqTownComment		/\s\*\s/hs=s+1,he=e-1
syn match tqSandComment		/\s,\s/hs=s+1,he=e-1
syn match tqShipComment		/\s[\&]\s/hs=s+1,he=e-1
syn match tqWaterComment	/\s\~\s/hs=s+1,he=e-1

syn match tqCave	/O/
syn match tqForest	/\^/
syn match tqGrass	/\./
syn match tqLava	/[\=]/
syn match tqMountain 	/A/
syn match tqSand	/,/
syn match tqShip	/[\&]/
syn match tqTown	/\*/
syn match tqWater	/\~/

syn region tqComment start="^#" skip="\\$" end="$" keepend contains=@tqCommentGrp,@Spell,tqCaveComment,tqForestComment,tqGrassComment,tqLavaComment,tqMountainComment,tqSandComment,tqShipComment,tqTownComment,tqWaterComment

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
  HiLink tqMountain		Mountain
  HiLink tqMountainComment	Mountain
  HiLink tqSand			Sand
  HiLink tqSandComment		Sand
  HiLink tqShip			Ship
  HiLink tqShipComment		Ship
  HiLink tqTown			Town
  HiLink tqTownComment		Town
  HiLink tqWater		Water
  HiLink tqWaterComment		Water

  HiLink tqComment      Comment

  delcommand HiLink
endif

let b:current_syntax = "tqmap"
