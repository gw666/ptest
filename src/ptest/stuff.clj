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

;installs drag-PNode handler onto left-mouse button
(def canvas1 (.getCanvas frame1))
(.setPanEventHandler canvas1 nil)

; TO GET CLICKED CARD TO MOVE TO TOP, must call .moveToFront
; on card; requires creating a custom event handler that does
; this, then calls PDragEventHandler.  gw 1/2/11

(.addInputEventListener canvas1 (PDragEventHandler.))

(def layer1 (.getLayer canvas1))



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
		   220 220 220   128
		   title-text "Monospaced" font-size  box-width
		   tenth tenth)

	title-height (.getHeight title-box)
	]
    (.translate body-box 0 (- title-height 1))
    (add! title-box body-box)
    (.setChildrenPickable title-box false)
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
;    (Thread/sleep (* seconds 1000))
;    (remove! layer1 card)
    ))