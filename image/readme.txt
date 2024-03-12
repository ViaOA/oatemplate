
Using Gimp (*.xcf)

Sizes:
	128 *Splash.png
	48 *Button.png
	24 *Bar.png

Files:
    image
    image\oa - gimp images
    image\oa\converted - saved as png for *Splash.png, *Button.png, *Bar.png



Process:
	pick colors
		color wheel
			http://www.mpsaz.org/skyline/staff/jamuth/project-1/

		jfc blue 6382bf	
		jfc light blue b8cfe5
		darker blue 0c1878			 
		darker red db2813
		darker orange fc9a04
		
get icons		
	www.iconbuddy.app
	www.thenounproject.com
	www.iconfinder.com

    www.streamlinehq.com/freebies/core-line-free/money-shopping?search=&icon=ico_03vnPoWFTUD4lb5y
        custom settings:  size=128,  stroke=8



convert image to ms icon
    https://icoconvert.com/


Create in image directory
	splash.xcf 
	logo.xcf   (150)
	bigicon.xcf   (64)
	icon.xcf   (32)
	iconClient.xcf (32)
	iconServer.xcf (32)

	template.xcf  (225)
		make bg transparent
		add rounded border (radius 12.0), line width 6
	button.xcf  (32)   sample
	bar.xcf     (20)   sample

gimp: use "file/export As" to convert *.xcf image to jpg/gif/etc


images need to be copied:
	directory: src/.../view/image
		splash.png 
		icon.gif
		iconServer.gif
		iconClient.gif

	directory: src/.../view/oa/image
		OABuilder generates the following icons based on the model UI tree.
			*Splash.png  (using template.xcf)
			*Button.png
			*Bar.png


	directory: src/.../report/html
		logo.jpg
		
	directory: src/.../help/image
		splash.png
		icon.gif	
	
Note:  use ^+[F11] in the Client to be able to click labels on glassframe	
	
	schoolsBar, Button, Splash.png

	
	
	
