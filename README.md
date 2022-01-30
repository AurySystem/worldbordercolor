# Recolorable world border
mod to adjust minecrafts world border color

add a worldborderconfig folder to your reasourcepack under your namespace
then add a json file with any name and and the following format
```Javascript
{
	"blending":"off", //Accepts "dark" or "off", anything else is vanilla blending 
	"customtrans":true, //overides the transparency with the specified value in alpha
	"alpha":155, //transparency of the world border, by default it's a factor based on the distance and stuff
	"color":"#00ff00", //primary color in hexadecimal
	"colorshrink":"#00fff0", //color used when the worldborder is shrinking
	"colorgrow":"#ffbf00" //color used when the worldborder is growing
}
```
