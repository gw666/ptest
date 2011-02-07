; file: core.clj (part of tech\cljprojects\ptest)
; last changed: 2/2/11

; HISTORY: 

; v1.3: New project, simplifying the code for creating an infocard;
;   On github, project ptest, branch simplified.
;
; v1.21: Converted program to be executable; first run slime (from 
;   ptest home dir, run 'lein swank'; then run (ns ptest.core), then
;   (load-file "src/ptest/core.clj") see git ptest project, tag
;   v0.0.1 in MASTER branch
; 
; v1.2: At the moment, this is not a workspace--it is a bin of Clojure
;   code that gets executed interactively as I play with my global
;   variables, add new functions, and tweak existing ones. I've wiped
;   out a running program from v1.1
;
; v1.1: Ditto 1.0, but using clj-piccolo2d calls.
;
; v1.0: This is an early working version of a project to put one infocard
;   onscreen and enable the user to move it around on the Piccolo desktop.


(comment
  ; remember to execute (ns ... ) first
  
  (def frame1 (PFrame.))
  (.setVisible frame1 true)
  (def canvas1 (.getCanvas frame1))
  (def layer1 (.getLayer canvas1))

  ; NO LONGER USING clj-piccolo2d LIBRARY 2/5/11 gw
  
  ;(def rect (rectangle 50 20 270 150))
  (def rect (PPath.))
  (def rect (.setPathToRectangle rect 50 20 270 150)) ; ??? need def again?
  (.setPaint rect (color 200 100 255 255))
  ;(set-paint! rect 200 100 255 255)
  (def small (text "Wakka!"))
  (.translate small 52 22)
  (add! rect small)
  (add! layer1 rect)




  (def clipping-rect (PClip.))
  (def small2 (PText. "Woo! This text is much longer and will go outside box"))
  (.translate small2 52 102)
  (.setPathToRectangle clipping-rect 50 100 270 150)
  (.setPaint clipping-rect (color 200 200 255 255))
  (.addChild clipping-rect small2)
  (.addChild layer1 clipping-rect)

  (.removeChild layer1 clipping-rect) ;use to start over
  (.removeChild layer1 rect) ;use to start over

  
  )


(ns ptest.core
  (:gen-class)
  (:import (edu.umd.cs.piccolo PCanvas PNode PLayer)
    (edu.umd.cs.piccolo.nodes   PPath PText)
    (edu.umd.cs.piccolo.event   PBasicInputEventHandler PDragEventHandler
      PDragSequenceEventHandler PInputEvent PInputEventFilter PPanEventHandler
      PZoomEventHandler)
    (edu.umd.cs.piccolo.util   PBounds)
    (edu.umd.cs.piccolox   PFrame)
    (edu.umd.cs.piccolox.nodes   PClip)
    (java.awt.geom   Dimension2D Point2D)
    (java.awt   BasicStroke Font GraphicsEnvironment Rectangle))
  (:use clj-piccolo2d.core))

; TO GET CLICKED CARD TO MOVE TO TOP, must (?) call .moveToFront
; on card; requires creating a custom event handler that does
; this, then calls PDragEventHandler.  gw 1/2/11

(declare txt)

(defn wrap
  "Return PText containing given text, font, font-size, x/y position,
 & width to wrap to"
  [text-str font-size x y wrap-width]
  ;
  ; text obj can't be remove!'d if you attempt to re-def it using
  ; another wrap-text; you must remove! it, re-def it, then add! it again
  ;
  (prn "at start of wrap-text")
;  (swank.core/break)
  (let [_font-name_ "Monospaced"
	wrapped-text (text)  ;empty at first
	height 0]   ;value used does not seem to matter
    (.setConstrainWidthToTextWidth wrapped-text false)
    (set-text! wrapped-text text-str)
    ;doesn't work unless *some* text exists
    (set-font! wrapped-text _font-name_ :plain font-size)
    (.setBounds wrapped-text x y wrap-width height)
    wrapped-text))

(defn clip-box
  ""
  [box-x box-y box-width box-height ;position, size of box
   r g b   a] ;red, green, blue, alpha values (all 0-255) for card bkgd color
  (prn "text-box")
  ;(swank.core/break)
					
  (let [clipping-rect (PClip.)]
    ;(swank.core/break)
    (.setPathToRectangle clipping-rect
			 box-x box-y
			 box-width box-height)
			 
    (set-paint! clipping-rect r g b a)
    clipping-rect))
    
(defn proto-card
    ""
    [clipping-box title-text body-text]
    ;a PClip representing entire card, text for title, body of card

    (let [_font-size_  12
	  tenth      (/ _font-size_ 10.0)
	  wrapped-body-text
	    (wrap body-text "Monospaced"
	]
    (.translate body-box 0 (- title-height 1))
    (add! title-box body-box)
    (.setChildrenPickable title-box false)
    title-box
    ))

(defn test-card [the-PFrame box-x box-y box-width box-height font-size seconds text-str]
  ;added text-str parameter (above)
    (prn "test-card")
  (let [card (proto-card	      
	     "Calming the mind becomes necessary" text-str
	     box-x box-y box-width box-height
	     font-size)]
    (prn "test-card")
    ; replaced 'layer1' with 'the-PFrame'
    (add! the-PFrame card)))



(defn testme [layer1 txt]
  (test-card layer1   0 0  270 124   12   10 txt)
  (test-card layer1   200 200  270 124   12   10 txt)
  )


(defn -main []
  (let [frame1 (PFrame.)
	canvas1 (.getCanvas frame1)
	txt  (str "We are at our least effective when we act in reaction to "
		  "whatever was the most recent thought in our head. When the "
		  "brain is very active, it spins from idea to idea with little "
		  "sense of connection between the two. Calming the mind becomes "
		  "necessary before we can hope to have any sense of mastery "
		  "over how we spend our time.")
	layer1 (.getLayer canvas1)
	dragger (PDragEventHandler.)]
    (.setVisible frame1 true)
    (.setMoveToFrontOnPress dragger true)

    ;installs drag-PNode handler onto left-mouse button
    (.setPanEventHandler canvas1 nil)
    (.addInputEventListener canvas1 dragger)
    ;(swank.core/break)
    (testme layer1 txt)))
