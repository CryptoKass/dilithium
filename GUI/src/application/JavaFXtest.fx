Stage {
title : "JavaFX App"
width: 300
height: 200
visible: true
scene: Scene {
fill:Color.SILVER
width:320
height:220
content: [
Rectangle {
x: 100 y: 50
width: 100 height: 100
arcWidth: 10
arcHeight : 10
fill: Color.RED
},
Text {
content: "JavaFX"
x:110 y:60
fill: Color.WHITE
font:Font {size: 16}
}
]
}
}