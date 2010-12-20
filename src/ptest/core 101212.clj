; file: core.clj (part of tech\cljprojects\ptest)
; last changed: 11/27/10

; HISTORY:
;
; v1.1: Ditto 1.0, but using clj-piccolo2d calls.
;
; v1.0: This is an early working version of a project to put one infocard
;   onscreen and enable the user to move it around on the Piccolo desktop.



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

(defn populate-frame
  "Populates the main PFrame used by the program."
  [the-frame]
  ; sample infocard
  (let [t "Slow down your day"
        t2 (str "We are at our least effective when we act in reaction to \n"
             "whatever was the most recent thought in our head. When the \n"
             "brain is very active, it spins from idea to idea with little\n"
             "sense of connection between the two. Calming the mind becomes \n"
             "necessary before we can hope to have any sense of mastery \n"
             "over how we spend our time.")
	font2 (Font. "Monospaced", Font/PLAIN, 20)
	title-text (text t)
        card-text (text t2)
        this-layer (.. the-frame getCanvas getLayer)
        ]

    (set-font! title-text "Monospaced" :plain 20)
    
    ; set cardBorder to enclose card-text node
    (let [title-text-bounds (.getGlobalBounds title-text)
          card-text-bounds (.getGlobalBounds card-text)]
      (.inset title-text-bounds -14 -14)  ; give it some border space
      (.inset card-text-bounds -14 -14)  ; give it some border space
      (let [title-border (PPath. title-text-bounds)
            card-border (PPath. card-text-bounds)
            grabber (rectangle 86 30 548 6)]

;	(swank.core/break)

        ; set hierarchy of layer to border and text nodes
	(add! this-layer grabber)
        (add! grabber title-border)
	(add! grabber card-border)
	(add! title-border title-text)
	(add! card-border card-text)
        (.animateToPositionScaleRotation title-border
          100 50 1 0 0)
        (.animateToPositionScaleRotation card-border
          100 50 1 0 0)

        (println "grabberBounds = " (.toString (.getGlobalBounds grabber)))
        (println "grabberBounds = " (.toString (width grabber))
		 (.toString (height grabber)))
        (println "title-border = "
          (.toString (.getGlobalBounds title-border)))
        (println "card-border = "
          (.toString (.getGlobalBounds card-border)))

        (println "title-text = "
          (.toString (.getGlobalBounds title-text)))
        (println "card-text = "
          (.toString (.getGlobalBounds card-text)))

        ; ensure that border and text move together
        (.setChildrenPickable grabber false)

        ; install basic drag event handler
        (.. the-frame getCanvas (addInputEventListener (PDragEventHandler.)))

        ; without next line, mouse drag on one node will cause movement of node
        ; plus pan of canvas, leading to illusion two nodes are linked
        ; by some scaling factor
        (.. the-frame getCanvas (setPanEventHandler nil))
        ))))

(defn -main []
  (let [main-frame (PFrame.)]
    (.setVisible main-frame true)
    (populate-frame main-frame)
    ))

(-main)

; ============================================================

(ns ptest.core)

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


(def frame1 (PFrame.))
(.setVisible frame1 true)

(def layer1 (.. frame1 getCanvas getLayer))

(def title1
     (let [title-text (text "Slow down your day")
	   title-text-bounds (.getGlobalBounds title-text)]
       (.inset title-text-bounds -14 -14)  ; give it some border space
       (let [title-border (PPath. title-text-bounds)]
	 (add! title-border title-text)
	 title-border)))

(add! layer1 title1)

(println "*Local* coords of title1 are " (.getX title1) (.getY title1))
; *Local* coords of title1 are  100.0 50.0

(.animateToPositionScaleRotation title1 100 50 1 0 0)

;(println "After (.aTPSRXOffset 100 50), XOffset and YOffset of title1 are " (.getXOffset title1) (.getYOffset title1))
; After (.aTPSRXOffset 100 50), XOffset and YOffset of title1 are  100.0 50.0

;(println (.getGlobalBounds title1))

(def body1 (text))
(. body1 setConstrainWidthToTextWidth false)
(. body1 setText )
(. body1 setBounds 50.0 50.0 400.0 800.0)
(add! layer1 body1)
(remove! layer1 title1)

(defn wrap-text
  "Return PText containing given text, font+size, position, & width"
  [text-str font-str size x y width]
  ;
  ; text obj can't be remove!'d if you attempt to re-def it using
  ; another wrap-text; remove! it, re-def it, then add! it again
  ;
  (let [wrapped-text (text)  ;empty at first
	height 0]   ;value used does not seem to matter
    (. wrapped-text setConstrainWidthToTextWidth false)
    (set-text! wrapped-text text-str)
    ;doesn't work unless *some* text exists
    (set-font! wrapped-text font-str :plain size)
    (. wrapped-text setBounds x y width height)
    wrapped-text))

(.getBounds wtxt)
(.getFullBounds wtxt)
(.getGlobalBounds wtxt)
(.getGlobalFullBounds wtxt)

(defn box-text
  ""
  [box-x box-y box-width box-height
   text-str font-str size x y width
   offset-x offset-y]
					
  (let [clipping-rect (PClip.)
;	layer (.. the-PFrame (getCanvas) (getLayer))
	text-height (.getHeight (text "just for testing"))
	offset-incr (/ text-height 10.0)
	wrapped-text (wrap-text text-str font-str size x y width)]
;    (swank.core/break)
    (.setPathToRectangle clipping-rect
			 box-x box-y
			 box-width box-height)
			 
    (.setOffset wrapped-text (* offset-incr 4) (* offset-incr 2))
    (add! clipping-rect wrapped-text)

    clipping-rect))

(box-text 0 0  465 300
	  txt "Monospaced" 18  0 0  300
	  10 10)

(defn o [the-PFrame width height fontsize seconds]
  (let [title (str "Calming the mind becomes necessary...")

	tenth (/ fontsize 10.0)
	body-box (box-text
		  10 10  width height
		  txt "Monospaced" fontsize  10 10  (- width 20)
		  tenth tenth)
	title-box (box-text
		   10 10  width (* tenth 12)
		   title "Monospaced" fontsize 10 10 width
		   tenth tenth)
	title-height (.getHeight title-box)
	]
;    (swank.core/break)
    (.translate body-box 0 (- title-height 1) )
    (add! title-box body-box)
;    (add! the-PFrame body-box)
    (add! the-PFrame title-box)
    (Thread/sleep (* seconds 1000))
    (remove! the-PFrame title-box)
;    (remove! the-PFrame body-box)
    (remove! title-box body-box)
    ))

(o layer1   465 300   18)