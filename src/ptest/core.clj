; file: core.clj (part of tech\cljprojects\ptest)
; last changed: 11/27/10

; HISTORY:

; v1.2: At the moment, this is not a workspace--it is a bin of Clojure
;   code that gets executed interactively as I play with my global
;   variables, add new functions, and tweak existing ones. I've wiped
;   out a running program from v1.1
;
; v1.1: Ditto 1.0, but using clj-piccolo2d calls.
;
; v1.0: This is an early working version of a project to put one infocard
;   onscreen and enable the user to move it around on the Piccolo desktop.


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

; don't use this; ;use translate or setOffset
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

; ============================================================
; a more general solution
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

(def txt (str "We are at our least effective when we act in reaction to "
             "whatever was the most recent thought in our head. When the "
             "brain is very active, it spins from idea to idea with little "
             "sense of connection between the two. Calming the mind becomes "
             "necessary before we can hope to have any sense of mastery "
             "over how we spend our time."))

(defn wrap-text
  "Return PText containing given text, font, font-size, x/y position, & width to wrap to"
  [text-str font-name font-size x y wrap-width]
  ;
  ; text obj can't be remove!'d if you attempt to re-def it using
  ; another wrap-text; remove! it, re-def it, then add! it again
  ;
  (let [wrapped-text (text)  ;empty at first
	height 0]   ;value used does not seem to matter
    (. wrapped-text setConstrainWidthToTextWidth false)
    (set-text! wrapped-text text-str)
    ;doesn't work unless *some* text exists
    (set-font! wrapped-text font-name :plain font-size)
    (. wrapped-text setBounds x y wrap-width height)
    wrapped-text))

(defn text-box
  ""
  [box-x box-y box-width box-height ;position, size of box
   r g b   a ;red, green, blue, alpha values (all 0-255) for card color
   ;text to put inside; font name, size; width to wrap to
   text-str font-name font-size wrap-width
   offset-x offset-y] ;small-increment offsets of text relative to box
  (prn "text-box")
;  (swank.core/break)
					
  (let [clipping-rect (PClip.)
	scratch-text (text "just for testing")
	ignored-value (set-font! scratch-text font-name :plain font-size)
	text-height (.getHeight scratch-text)
	offset-incr (/ text-height 10.0)
	wrapped-text
	  (wrap-text text-str font-name font-size box-x box-y wrap-width)]
 ;   (swank.core/break)
    (.setPathToRectangle clipping-rect
			 box-x box-y
			 box-width box-height)
			 
    (.setOffset wrapped-text (* offset-x 4) (* offset-y 2))
    (set-paint! clipping-rect r g b a)
    
    (add! clipping-rect wrapped-text)

    clipping-rect))

(defn one-line-box
  ""
  [box-x box-y box-width box-height ;position, size of box
   r g b   a ;red, green, blue, alpha values (all 0-255) for card color
   ;text to put inside; font name, size; width to wrap to
   text-str font-name font-size wrap-width
   offset-x offset-y] ;small-increment offsets of text relative to box
  (prn "one-line-box")
;  (swank.core/break)

  (let [box (rectangle box-x box-y box-width box-height)
	my-text (text text-str)
	ignored-value (set-font! my-text font-name :plain font-size)
	text-width (.getWidth my-text)
	text-height (.getHeight my-text)
	offset-incr (/ text-height 10.0)]

    (if (> text-width box-width)
      START HERE
    
    (.setOffset my-text (+ (* offset-x 4) box-x)
		(+ (* offset-y 1.4) box-y))
    (set-paint! box r g b   a)
    (add! box my-text)
;  (swank.core/break)
    box))
    
(defn proto-card
    ""
    [title-text body-text ;text for title, body of card
     box-x box-y box-width box-height ;x/y position, size of body
     font-size] ;size of font--font type is fixed
    (prn "proto-card")
;    (swank.core/break)

  (let [tenth (/ font-size 10.0)
	body-box (text-box
		  box-x box-y box-width box-height
		  255 255 240   255
		  body-text "Monospaced" font-size  (- box-width 20)
		  tenth tenth)
	title-box (one-line-box
		   box-x box-y  box-width (* tenth 12)
		   255 255 240   255
		   title-text "Monospaced" font-size  box-width
		   tenth tenth)

	title-height (.getHeight title-box)
	]
    (.translate body-box 0 (- title-height 1))
    (add! title-box body-box)
    title-box
    ))

(defn test-card [the-PFrame box-x box-y box-width box-height font-size seconds]
    (prn "test-card")
;    (swank.core/break)
  (let [card (proto-card	      
	     "Calming the mind becomes necessary" txt
	     box-x box-y box-width box-height
	     font-size)]
    (prn "test-card")
;    (swank.core/break)
    (add! layer1 card)
;    (prn "test-card 2")
;    (swank.core/break)
    (Thread/sleep (* seconds 1000))
    (remove! layer1 card)))

(defn test []
  (test-card layer1   0 0  270 124   12   10)
  (test-card layer1   200 200  270 124   12   10)
  )