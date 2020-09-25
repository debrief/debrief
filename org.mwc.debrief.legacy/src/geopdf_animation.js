    var animate = null;
    var animationRunning = false;

    // Colours for the UI components
    var lightBlue = new Array("RGB", 0.815, 0.886, 0.949);
    var darkBlue = new Array("RGB", 0.443, 0.662, 0.854);

    var current_index = 0;
    var firstRun = true;
    var factor = null;
    var dpi = 99;
    var lastPageViewX = null;
    var lastPageViewY = null;
    var lastPageViewZoom = null;
    var lastSliderButtonLocation = 0;

    // List of non-interactive layers
    !!NONINTERACTLAYERS

    // Array of objects containing the name of the timestamp
    // and a name of the OCG to turn on for that timestep.
    // This is altered by the code in the MAIN section at the bottom
    // which puts references to the actual OCG objects in the array
    !!JS_TIMESTAMPS

    function goToFirstTimestep() {
        goToTimestepIndex(0);
        markSliderPart(0);
        lastSliderButtonLocation = 0;
    }

    function goToTimestepIndex(idx) {
        // We can make this efficient because we know that everything is in time
        // order, and there will be blocks of timestamps that are on or off

        // If the next timestamp to the right is on, then continue searching
        // to the right turning timestamps off, until we get to one that is off
        // Once we find one that is off we know that all the rest to the right
        // of that one will also be off, so we can stop there.
        var upwards_idx = idx + 1;
        while ((upwards_idx < timestamps.length) && (timestamps[upwards_idx].ocg.state == true)) {
            timestamps[upwards_idx].ocg.state = false;
            upwards_idx = upwards_idx + 1;
        }

        // Same as above, but going to the left - if we know it is off then we need
        // to turn it on, until we find a block of ones that are on
        var downwards_idx = idx - 1;
        while ((downwards_idx >= 0) && (timestamps[downwards_idx].ocg.state == false)) {
            timestamps[downwards_idx].ocg.state = true;
            downwards_idx = downwards_idx - 1;
        }
    
        current_index = idx;

        // Update the displayed time
        var txt = this.getField("txtTime");
        txt.value = timestamps[current_index].name;

        this.dirty = false;
    }

    function toggleObjectVisible(objName) {
        // Takes an object name, gets the object, and toggles its visibility
        var obj = this.getField(objName);
        if (obj.display == display.visible) {
            obj.display = display.hidden;
        } else {
            obj.display = display.visible;
        }
    }

    function toggleDynamic() {
        // Toggles the dynamic UI components by:
        //  - toggling the state of the toggle button
        //  - turning on/off the static/dynamic layers
        //  - making the other UI components visible/invisible
        // We use the borderStyle of the button to tell us what
        // state it is in
        // If it is currently Inset (border.i) then it looks 'pressed' and we
        // want to 'unpress' it
        if (event.target.borderStyle == border.i) {
            // Perform Pop Up Actions - ie. switch back to non-interactive

            event.target.borderStyle = border.b; // Bevelled border - looks unpressed
            // Restore Up Colors
            event.target.fillColor = lightBlue;

            unmarkSliderPart(lastSliderButtonLocation);

            for (i = 0; i < nonInteractiveLayers.length; i += 1) {
                getOCGByName(nonInteractiveLayers[i]).state = true;
            }
            getOCGByName("Interactive Layers").state = false;
        } else {
            // Perform Push Down Actions - ie. switch to interactive

            event.target.borderStyle = border.i; // Inset border - looks pressed
            // Darken Down Colors
            event.target.fillColor = darkBlue;

            for (i = 0; i < nonInteractiveLayers.length; i += 1) {
                getOCGByName(nonInteractiveLayers[i]).state = false;
            }
            getOCGByName("Interactive Layers").state = true;
            
            goToFirstTimestep();
        }

        toggleObjectVisible("btnPlayPause");
        toggleObjectVisible("btnNext");
        toggleObjectVisible("btnPrev");
        toggleObjectVisible("txtTime");
        for (i = 0; i < timestamps.length; i += 1) {
            toggleObjectVisible("btnSlider_" + i);
        }

        this.dirty = false;
    }

    function nextTimestep() {
        if (current_index >= 0) {
            unmarkSliderPart(lastSliderButtonLocation);
        }
        current_index += 1;

        if (current_index > timestamps.length - 1) {
            // If we're trying to go past the end of the timestamps array then

            // Reset back to the last element
            current_index = timestamps.length - 1;

            // Stop the animation
            if (animate) {
                app.clearInterval(animate);
                animationRunning = false;
            }
            
            // Reset the Play/Pause button state
            var btn = this.getField("btnPlayPause");
            btn.borderStyle = border.b;
            btn.buttonSetCaption(">");
            btn.fillColor = lightBlue;
            return;
        }

        // Get the next OCG (layer) and turn it on
        timestamps[current_index].ocg.state = true;

        // Update the displayed time
        var txt = this.getField("txtTime");
        txt.value = timestamps[current_index].name;

        markSliderPart(current_index);
        lastSliderButtonLocation = current_index;

        this.dirty = false;
    }

    function prevTimestep() {
        // Turn off the current OCG (layer)
        timestamps[current_index].ocg.state = false;
        unmarkSliderPart(lastSliderButtonLocation);

        current_index -= 1;

        if (current_index < 0) {
            current_index = 0;
        }

        // Update the displayed time
        var txt = this.getField("txtTime");
        txt.value = timestamps[current_index].name;

        markSliderPart(current_index);
        lastSliderButtonLocation = current_index;

        this.dirty = false;
    }

    function playPauseAnimation() {
        // Plays/Pauses the animation - called from the main playPause function
        if (animationRunning) {
            app.clearInterval(animate);
            animationRunning = false;
        }
        else {
            animate = app.setInterval("nextTimestep()", 1000);
            animationRunning = true;
        }
    }

    function playPause() {
        // Deals with changing the state of the Play/Pause button and
        // then actually plays/pauses the animation
        // Again, this uses the button border style to tell us whether we need to
        // play or pause
        if (event.target.borderStyle == border.i) {
            // Perform Pop Up Actions
            event.target.borderStyle = border.b; // Bevelled - ie. unpressed
            event.target.buttonSetCaption(">");
            // Restore Up Colors
            event.target.fillColor = lightBlue;
            playPauseAnimation();
        } else {
            // Perform Push Down Actions
            event.target.borderStyle = border.i; // Inset - ie. pressed
            event.target.buttonSetCaption("||");
            // Darken Down Colors
            event.target.fillColor = darkBlue;
            playPauseAnimation();
        }
    }

    function getOCGByName(name) {
        // Gets an OCG (a layer) by name
        var ocgs = this.getOCGs();

        for (var i = 0; i < ocgs.length; i += 1) {
            if (ocgs[i].name == name) {
                return ocgs[i];
            }
        }

        return false;
    }

    function markSliderPart(index) {
        var btn = this.getField("btnSlider_" + index);
        btn.fillColor = darkBlue;
    }

    function unmarkSliderPart(index) {
        var btn = this.getField("btnSlider_" + index);
        btn.fillColor = lightBlue;
    }


    function calcNewBasePosition() {
        // Calculate the new base X and Y position for the UI components from the current
        // scroll and zoom position

        if (firstRun) {
            // On the first run only

            // Print these elements to the console. We have to do this to initialise
            // the media component - rather strange, but it works.
            console.println(app.media);
            console.println(this.media);

            // Get the DPI, if it's invalid then set to the default of 110
            dpi = getDPI();
            if (dpi == 0) {
                dpi = 110;
            }

            // The factor of 0.732 here is established by trial and error
            // and seems to be the conversion factor between the units of pageViewX/Y
            // and actual page units
            factor = 0.732 / (dpi / 99);

            firstRun = false;
        }

        var pgRect = this.getPageBox("Crop", 0);

        var curView = eval(this.viewState.toSource());

        // If nothing has changed since last time, then return
        if ((curView.pageViewX == lastPageViewX) & (curView.pageViewY == lastPageViewY) & (curView.pageViewZoom == lastPageViewZoom)) {
            return null;
        }

        // Keep track of the last values
        lastPageViewX = curView.pageViewX;
        lastPageViewY = curView.pageViewY;
        lastPageViewZoom = curView.pageViewZoom;       

        // Get a zoom factor which is either the current zoom, or 1 if the current zoom is less than 1
        var zoomFactor = curView.pageViewZoom < 1 ? 1 : curView.pageViewZoom;

        // Calculate the new X and Y base positions based on the factor, the zoom and the current X and Y coords
        var yPos = curView.pageViewY >=0 ? (curView.pageViewY + 4) * factor / curView.pageViewZoom : 0;
        var xPos = curView.pageViewX >=0 ? (curView.pageViewX + 4) * factor / curView.pageViewZoom : 0;

        // the Y values for pageViewY count down from the top of the page, whereas the Y values for locating objects
        // on the page count up from the bottom. Take away the co-ords of the top to switch between them.
        yPos = pgRect[1] - yPos;

        return [xPos, yPos, zoomFactor];
    }

    function moveField(fldName, xPos, yPos, zoomFactor) {
        var fld = this.getField(fldName);
        var rect = fld.rect;

        // Calculate the new position, based on the stored offsets and sizes, the new position and the zoom scaling factor
        rect[0] = xPos + (fld._xOffset / zoomFactor);
        rect[1] = yPos + (fld._yOffset / zoomFactor);
        rect[2] = xPos + (fld._xOffset / zoomFactor) + (fld._xSize / zoomFactor);
        rect[3] = yPos + (fld._yOffset / zoomFactor) - (fld._ySize / zoomFactor);

        fld.rect = rect;

        // Scale the text size by the zoom factor too
        fld.textSize = fld._origTextSize / zoomFactor;        
    }

    function moveAllFields() {
        // Delays visually updating the fields while we move them all
        this.delay = true;
        newPos = calcNewBasePosition();

        // If zoom/scroll position hasn't changed then do nothing
        if (newPos == null) {
            this.delay = false;
            return;
        }

        [xPos, yPos, zoomFactor] = newPos;

        // Move each of the main fields
        moveField("btnPrev", xPos, yPos, zoomFactor);
        moveField("btnDynamicToggle", xPos, yPos, zoomFactor);
        moveField("btnNext", xPos, yPos, zoomFactor);
        moveField("btnPlayPause", xPos, yPos, zoomFactor);
        moveField("txtTime", xPos, yPos, zoomFactor);

        // Move the many buttons that make up the slider
        for (i = 0; i < timestamps.length; i += 1) {
            moveField("btnSlider_" + i, xPos, yPos, zoomFactor);
        }

        // Updates the display of all fields now we've finished moving them
        this.delay = false;
        this.dirty = false;
    }

    function createButton(cName, caption, nPage, xOffset, yOffset, xSize, ySize, hidden, jsCode) {
        // Acquire the crop box (visible area) for the current page
        var pgRect = this.getPageBox("Crop", nPage);
        // Create array for size/location
        var fldRect = [];
        fldRect[0] = pgRect[0] + xOffset;
        fldRect[1] = pgRect[0] + yOffset + ySize;
        fldRect[2] = pgRect[0] + xOffset + xSize;
        fldRect[3] = pgRect[0] + yOffset;
        // Create the button
        var oFld = this.addField( cName , "button", nPage, fldRect);
        // Set the properties
        if (oFld != null) {
            oFld.buttonSetCaption(caption);
            oFld.borderStyle == border.i;
            oFld.strokeColor = color.black;
            oFld.textColor = color.black;
            oFld.textSize = 20;
            oFld.fillColor = lightBlue;
            oFld.lineWidth = 0;
            if (hidden) {
                oFld.display = display.hidden;
            }
            oFld.setAction("MouseUp", jsCode);

            // Store offsets and size in attributes so we can use them later to reposition the button
            oFld._xOffset = xOffset;
            oFld._yOffset = yOffset;
            oFld._xSize = xSize;
            oFld._ySize = ySize;
            oFld._origTextSize = oFld.textSize;
        }
        return oFld;
    }

    function createSliderButton(cName, caption, nPage, xOffset, yOffset, xSize, ySize, hidden, jsCode) {
        // Acquire the crop box (visible area) for the current page
        var pgRect = this.getPageBox("Crop", nPage);
        // Create array for size/location
        var fldRect = [];
        fldRect[0] = pgRect[0] + xOffset;
        fldRect[1] = pgRect[0] + yOffset + ySize;
        fldRect[2] = pgRect[0] + xOffset + xSize;
        fldRect[3] = pgRect[0] + yOffset;
        // Create the button
        var oFld = this.addField( cName , "button", nPage, fldRect);
        // Set the properties
        if (oFld != null) {
            oFld.buttonSetCaption(caption);
            oFld.borderStyle == border.s; // Solid border
            oFld.strokeColor = color.transparent;
            oFld.textColor = color.black;
            oFld.fillColor = lightBlue;
            oFld.lineWidth = 0;
            if (hidden) {
                oFld.display = display.hidden;
            }
            oFld.setAction("MouseEnter", jsCode);

            // Store offsets and size in attributes so we can use them later to reposition the button
            oFld._xOffset = xOffset;
            oFld._yOffset = yOffset;
            oFld._xSize = xSize;
            oFld._ySize = ySize;
            oFld._origTextSize = oFld.textSize;
        }
        return oFld;
    }

    function createTextbox(cName, contents, nPage, xOffset, yOffset, xSize, ySize, hidden) {
        // Acquire the crop box (visible area) for the current page
        var pgRect = this.getPageBox("Crop", nPage);
        // Create array for size/location
        var fldRect = [];
        fldRect[0] = pgRect[0] + xOffset; // left x
        fldRect[1] = pgRect[0] + yOffset + ySize; // top y
        fldRect[2] = pgRect[0] + xOffset + xSize; // right x
        fldRect[3] = pgRect[0] + yOffset; // bottom y
        // Create Textbox on page
        var oFld = this.addField( cName , "text", nPage, fldRect);
        // Setup Textbox's Properties
        if (oFld != null) {
            oFld.value = contents;
            oFld.readonly = true;
            oFld.alignment = "center";
            oFld.borderStyle == border.i;
            oFld.textColor = color.green;
            oFld.textSize = 16;
            oFld.fillColor = color.black;
            oFld.lineWidth = 0;
            if (hidden) {
                oFld.display = display.hidden;
            }

            // Store offsets and size in attributes so we can use them later to reposition the button
            oFld._xOffset = xOffset;
            oFld._yOffset = yOffset;
            oFld._xSize = xSize;
            oFld._ySize = ySize;
            oFld._origTextSize = oFld.textSize;
        }
        return oFld;
    }

    function getDPI(doc){
        // Taken from https://community.adobe.com/t5/acrobat/is-there-a-way-to-read-the-user-s-page-display-resolution-preference-setting-from-javascript/td-p/9562886?page=1
        if (doc == undefined) {
            doc = this;
        }
        // get current view
        var refView = eval(doc.viewState.toSource());
        var pBox = doc.getPageBox("Crop", 0);
        // Determine page width in inches
        var pWidth = ((pBox[2] - pBox[0]) / 72);
        // If pageViewZoomType 2 is "fit width" and that's all we need.
        if (refView.pageViewZoomType != 2) {
            // save current view to return to it so user won't notice pageViewZoomType change.
            var curView = eval(doc.viewState.toSource());
            // set control values to sample the zoom level at fitwidth on the target page. refView.pageViewPageNum= n;
            refView.pageViewX = 0;  //Ensure we don't somehow wind up on another page.  Though I suspect a pageViewX of > 0 is impossible on fit width...
            refView.pageViewY = 0;  //Ensure we don't somehow wind up on another page.
            refView.pageViewZoomType = 2;
            // update view to get new zoom
            doc.viewState = refView;
            // grab a copy to inspect
            refView = eval(viewState.toSource());
            // restore previous view
            doc.viewState = curView;
        }

        // initialize the media object to read pageWindowRect, because AcrobatJS...
        //compare pixel width of application page window area, the current zoom level,
        // and the page width in inches to get user display DPI.  Round to clean up FP errors; but +/- 1 PPI isn't the end of the world.
        userDPI  = Math.round((doc.pageWindowRect[2] - doc.pageWindowRect[0]) / refView.pageViewZoom / pWidth);
        return userDPI;
    }

    function onSliderButtonEnter(index) {
        unmarkSliderPart(lastSliderButtonLocation);
        markSliderPart(index);
        goToTimestepIndex(index);
        lastSliderButtonLocation = index;
    }

    function addOCGsToArray() {
        var ocgs = this.getOCGs();
        var ocg_name_to_ocg = {};

        // Iterate only once over the list of OCGs, creating a dict
        // mapping OCG name to OCG object
        for (var i = 0; i < ocgs.length; i += 1) {
            ocg_name_to_ocg[ocgs[i].name] = ocgs[i];
        }

        // Iterate over the list of timestamps, setting the .ocg
        // property to the OCG object using the dict created above
        for (i = 0; i < timestamps.length; i += 1) {
            timestamps[i].ocg = ocg_name_to_ocg[timestamps[i].ocg_name];
        }
    }

    //
    // Main code - run on document open
    //

    // Put the actual OCG references into the timestamps array
    addOCGsToArray();

    // Create main UI components
    var overall_left = 10;
    var overall_top = -10;
    createButton("btnDynamicToggle", "", 0, overall_left, overall_top - 25, 36, 36, false, "toggleDynamic();");
    createButton("btnPrev", "<<", 0, overall_left + 40, overall_top - 25, 36, 36, true, "prevTimestep();");
    createButton("btnPlayPause", ">", 0, overall_left + (2 * 40), overall_top - 25, 36, 36, true, "playPause();");
    createButton("btnNext", ">>", 0, overall_left + (3 * 40), overall_top - 25, 36, 36, true, "nextTimestep();");
    createTextbox("txtTime", "", 0, overall_left, overall_top - 65, 156, 20, true);

    // Set font and text to get plane logo on toggle button
    var btn = this.getField("btnDynamicToggle");
    btn.textFont = font.ZapfD;
    btn.buttonSetCaption("(");

    // Create many small buttons for the slider
    var smallButtonSize = 156 / timestamps.length;
    for (i = 0; i < timestamps.length; i += 1) {
        createSliderButton("btnSlider_" + i, "", 0, overall_left+(smallButtonSize*i), overall_top, smallButtonSize, 20, true, "onSliderButtonEnter(" + i + ");");
    }

    // Move all fields at start up
    moveAllFields();

    // Set up timer to move all fields at 1 second intervals
    moveTimer = app.setInterval("moveAllFields()", 1000);
    // Stop timer on document close
    this.setAction("WillClose", "app.clearInterval(moveTimer);");

    // Mark doc as not having changed, so it doesn't say it needs saving
    this.dirty = false;