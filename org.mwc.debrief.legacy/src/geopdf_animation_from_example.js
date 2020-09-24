    var animate = null;
    var animationRunning = false;

    // Colours for the UI components
    var lightBlue = new Array("RGB", 0.815, 0.886, 0.949);
    var darkBlue = new Array("RGB", 0.443, 0.662, 0.854);

    var current_index = 0;

    // Array of objects containing the name of the timestamp
    // and a name of the OCG to turn on for that timestep.
    // This is altered by the code directly beneath it, to add
    // an actual reference to the OCG too
    !!JS_TIMESTAMPS

    // Put the actual OCG references into the timestamps array
    // (this isn't in a function, so will just run on document load)
    for (i = 0; i < timestamps.length; i += 1) {
        var ocg = getOCGByName(timestamps[i].ocg_name);
        timestamps[i].ocg = ocg;
    }

    function goToFirstTimestep() {
        // Goes to the first timestep - ie. hides all layers except for the first one
        for (var i = 0; i < timestamps.length; i += 1) {
            timestamps[i].ocg.state = false
        }
        current_index = -1;
        nextTimestep();

        this.dirty = false;
    }

    function goToLastTimestep() {
        // Goes to the last timestep - ie. shows all layers
        for (var i = 0; i < timestamps.length; i += 1) {
            timestamps[i].ocg.state = true
        }

        current_index = timestamps.length - 1

        // Update the displayed time
        var txt = this.getField("txtTime");
        txt.value = timestamps[current_index].name;

        this.dirty = false;
    }

    function goToTimestepIndex(idx) {
        for (var i = 0; i < timestamps.length; i += 1) {
            if (i <= idx) {
                timestamps[i].ocg.state = true;
            } else {
                timestamps[i].ocg.state = false;
            }
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
        //  - going to the first or last timestep
        //  - making the other UI components visible/invisible
        if (event.target.borderStyle == border.i) {
            // Perform Pop Up Actions
            event.target.borderStyle = border.b;
            // Restore Up Colors
            event.target.fillColor = lightBlue;
            
            goToLastTimestep();

            toggleObjectVisible("btnPlayPause");
            toggleObjectVisible("btnNext");
            toggleObjectVisible("btnPrev");
            toggleObjectVisible("txtTime");
            for (i = 0; i < timestamps.length; i += 1) {
                toggleObjectVisible("btnSlider_" + i);
            }
        } else {
            // Perform Push Down Actions
            event.target.borderStyle = border.i;
            // Darken Down Colors
            event.target.fillColor = darkBlue;
            
            goToFirstTimestep();

            toggleObjectVisible("btnPlayPause");
            toggleObjectVisible("btnNext");
            toggleObjectVisible("btnPrev");
            toggleObjectVisible("txtTime");
            for (i = 0; i < timestamps.length; i += 1) {
                toggleObjectVisible("btnSlider_" + i);
            }
        }
        this.dirty = false;
    }

    function nextTimestep() {
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

        this.dirty = false;
    }

    function prevTimestep() {
        // Turn off the current OCG (layer)
        timestamps[current_index].ocg.state = false;

        current_index -= 1;

        if (current_index < 0) {
            current_index = 0;
        }

        // Update the displayed time
        var txt = this.getField("txtTime");
        txt.value = timestamps[current_index].name;

        markSliderPart(current_index);

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
        if (event.target.borderStyle == border.i) {
            // Perform Pop Up Actions
            event.target.borderStyle = border.b;
            event.target.buttonSetCaption(">");
            // Restore Up Colors
            event.target.fillColor = lightBlue;
            playPauseAnimation();
        } else {
            // Perform Push Down Actions
            event.target.borderStyle = border.i;
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
        // Make all buttons light blue
        for (i = 0; i < timestamps.length; i += 1) {
            var btn = this.getField("btnSlider_" + i);
            btn.fillColor = lightBlue;
        }

        var btn = this.getField("btnSlider_" + index);
        btn.fillColor = darkBlue;
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
            oFld.borderStyle == border.i
            oFld.strokeColor = color.black;
            oFld.textColor = color.black;
            oFld.textSize = 20;
            oFld.fillColor = lightBlue;
            oFld.lineWidth = 0;
            if (hidden) {
                oFld.display = display.hidden
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
            oFld.borderStyle == border.s;
            oFld.strokeColor = color.transparent;
            oFld.textColor = color.black;
            oFld.fillColor = lightBlue;
            oFld.lineWidth = 0;
            if (hidden) {
                oFld.display = display.hidden
            }
            oFld.setAction("MouseEnter", jsCode);
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
                oFld.display = display.hidden
            }
            oFld._xOffset = xOffset;
            oFld._yOffset = yOffset;
            oFld._xSize = xSize;
            oFld._ySize = ySize;
            oFld._origTextSize = oFld.textSize;
        }
        return oFld;
    }

    function getDPI(doc){
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
        console.println(this.media);
        //compare pixel width of application page window area, the current zoom level,
        // and the page width in inches to get user display DPI.  Round to clean up FP errors; but +/- 1 PPI isn't the end of the world.
        userDPI  = Math.round((doc.pageWindowRect[2] - doc.pageWindowRect[0]) / refView.pageViewZoom / pWidth);
        console.println("\nUser DPI: "+userDPI );
        return userDPI;
    }

    // Create UI components
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


    var smallButtonSize = 156 / timestamps.length;
    for (i = 0; i < timestamps.length; i += 1) {
        createSliderButton("btnSlider_" + i, "", 0, overall_left+(smallButtonSize*i), overall_top, smallButtonSize, 20, true, "markSliderPart(" + i + "); goToTimestepIndex(" + i + ");")
    }

    // Mark doc as not having changed, so it doesn't say it needs saving
    this.dirty = false;

    var firstRun = true;
    var factor = null;
    var dpi = 99;
    var lastPageViewX = null;
    var lastPageViewY = null;
    var lastPageViewZoom = null;

    function calcNewBasePosition() {
        if (firstRun) {
            console.println(app.media);
            console.println(this.media);
            dpi = getDPI();
            if (dpi == 0) {
                dpi = 110;
            }

            factor = 0.732 / (dpi / 99);

            firstRun = false;
        }

        var pgRect = this.getPageBox("Crop", 0);

        var xOffset = 0;
        var yOffset = 0;

        var curView = eval(this.viewState.toSource());

        if ((curView.pageViewX == lastPageViewX) & (curView.pageViewY == lastPageViewY) & (curView.pageViewZoom == lastPageViewZoom)) {
            return null;
        }

        lastPageViewX = curView.pageViewX;
        lastPageViewY = curView.pageViewY;
        lastPageViewZoom = curView.pageViewZoom;       

        var zoomFactor = curView.pageViewZoom < 1 ? 1 : curView.pageViewZoom;
        var yPos = curView.pageViewY >=0 ? (curView.pageViewY + 4) * factor / curView.pageViewZoom : 0;
        var xPos = curView.pageViewX >=0 ? (curView.pageViewX + 4) * factor / curView.pageViewZoom : 0;

        yPos = pgRect[1] - yPos;

        return [xPos, yPos, zoomFactor]
    }

    function moveField(fldName, xPos, yPos, zoomFactor) {
        var fld = this.getField(fldName);
        var rect = fld.rect;

        rect[0] = xPos + (fld._xOffset / zoomFactor);
        rect[1] = yPos + (fld._yOffset / zoomFactor);
        rect[2] = xPos + (fld._xOffset / zoomFactor) + (fld._xSize / zoomFactor);
        rect[3] = yPos + (fld._yOffset / zoomFactor) - (fld._ySize / zoomFactor);

        fld.rect = rect;

        fld.textSize = fld._origTextSize / zoomFactor;

        //if (zoomFactor > 4) {
        //    fld.lineWidth = 0;
        //} else {
        //    fld.lineWidth = 1;
        //}
        
    }

    function moveAllFields() {
        newPos = calcNewBasePosition();

        if (newPos == null) {
            return;
        }

        [xPos, yPos, zoomFactor] = newPos;

        moveField("btnPrev", xPos, yPos, zoomFactor);
        moveField("btnDynamicToggle", xPos, yPos, zoomFactor);
        moveField("btnNext", xPos, yPos, zoomFactor);
        moveField("btnPlayPause", xPos, yPos, zoomFactor);
        moveField("txtTime", xPos, yPos, zoomFactor);

        for (i = 0; i < timestamps.length; i += 1) {
            moveField("btnSlider_" + i, xPos, yPos, zoomFactor);
        }
    }

    // Move all fields at start up
    moveAllFields();

    moveTimer = app.setInterval("moveAllFields()", 1000);
    this.setAction("WillClose", "app.clearInterval(moveTimer);");
