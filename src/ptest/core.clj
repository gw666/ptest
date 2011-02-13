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
;
;
; NOTE: _underlines_ connote a variable that acts like a *constant*
;   for this program but can be changed if needed by altering source code
;
; NO LONGER USING clj-piccolo2d LIBRARY 2/5/11 gw
;

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
    (java.awt   BasicStroke Color Font GraphicsEnvironment Rectangle)))

(defn wrap
  "Return PText containing given text & width to wrap to"
  [text-str wrap-width]
  ;
  ; during debugging:
  ; text obj can't be remove!'d if you attempt to re-def it using
  ;  wrap again; you must remove! it, re-def it, then add! it again
  ;
  (prn "at start of new wrap-text")
;  (swank.core/break)
  
  (let [wrapped-text (PText.)]
    (.setConstrainWidthToTextWidth wrapped-text false)
    (.setText wrapped-text text-str)
    (.setBounds wrapped-text 0 0 wrap-width 100)
    wrapped-text))

(defn infocard
  "Create basic infocard (ready to be added as the child of a layer)"

  ;NOTE: card is transparent, can't be moved 110211
  [box-x box-y box-width box-height title-text body-text] ;position, size of box
  (prn "reached infocard")

  (let [cbox (PClip.)
	title (PText. title-text)
	indent-x 5
	indent-y 4
	body (wrap body-text (- box-width (quot indent-x 2)))
	line-height 21
	divider-height 22
	end-x (+ box-x box-width)
	line (PPath/createLine box-x (+ box-y divider-height)
			       end-x (+ box-y divider-height))
	backgd-color (Color. 250 250 250)
	divider-color (Color. 255 100 100)
	]
    ;(swank.core/break)
    (.translate title (+ box-x indent-x) (+ box-y indent-y))
    (.translate body (+ box-x indent-x) (+ box-y indent-y line-height))
    (.setPathToRectangle cbox
			 box-x box-y
			 box-width box-height)
    (.setPaint cbox backgd-color)
    (.setStrokePaint line divider-color)
    (.addChild cbox title)
    (.addChild cbox line)
    (.addChild cbox body)
    (.setChildrenPickable cbox false)
    ;(swank.core/break)			 
    cbox))

(declare title-text)
(declare body-text)

  ;(:use clj-piccolo2d.core)
  


(defn testme [the-layer title-text body-text]
  (def card (infocard 50 100 270 175 title-text body-text))
  (def card2 (infocard 100 150 270 175 title-text body-text))
  (.addChild the-layer card)
  (.addChild the-layer card2))


(defn -main []
  (let [frame1 (PFrame.)
	canvas1 (.getCanvas frame1)
	title-text "\"Boof, boof, boof!\", says the dog! Boof, boof, boof!\", says the dog! Boof, boof, boof!\", says the dog!"
	body-text  "Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Bye!"
	layer1 (.getLayer canvas1)
	dragger (PDragEventHandler.)]
    (prn "reached -main 1")
    
    (.setVisible frame1 true)
    (.setMoveToFrontOnPress dragger true)

    ;installs drag-PNode handler onto left-mouse button
    (.setPanEventHandler canvas1 nil)
    (.addInputEventListener canvas1 dragger)
    ;(swank.core/break)
    (testme layer1 title-text body-text)))


;======================================================

(comment
  ; remember to execute (ns ... ) first
  
  (def frame1 (PFrame.))
  (.setVisible frame1 true)
  (def canvas1 (.getCanvas frame1))
  (def layer1 (.getLayer canvas1))

  
  
  ;(def rect (rectangle 50 20 270 150))
  (def rect (PPath.))
  (def rect (.setPathToRectangle rect 50 20 270 150)) ; ??? need def again?
  (.setPaint rect (color 200 100 255 255))
  ;(set-paint! rect 200 100 255 255)
  (def small (text "Wakka!"))
  (.translate small 52 22)
  (add! rect small)
  (add! layer1 rect)




  (def cr (PClip.))
  (.setVisible cr true)
  (def bt "Woo! This text is much longer and will go outside box")
  (def tx (PText. bt))
  (def t2 "Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Woo! This text is much longer and will go outside box. Bye!")
  (.translate tx 52 102)
  (.setPathToRectangle cr 50 100 270 150)
  (.setPaint cr (Color. 200 200 255 255))
  (.addChild cr tx)
  (.addChild layer1 cr)

  (def wt2 (wrap t2 12 52 116 245))
  (.addChild cr wt2)

  (.removeChild layer1 cr) ;use to start over
  (.removeChild layer1 rect) ;use to start over
  (.removeChild cr tx) ;use to start over
  (.removeChild cr wt2) ;use to start over

  ; experiment 1, 110211
  (def cr (PClip.))
  (def tx (PText. "Woo! This text is much longer and will go outside box"))
  (.translate tx 52 102)
  (.setPathToRectangle cr 50 100 270 150)
  (.addChild cr tx)
  (.addChild layer1 cr)

;result: nothing happens as long as window is not selected; after it is,
;there is a delay of approx 1 second, then cr appears with tx within it;
;cr is at 50 100 relative to window, tx is in "right" place, at 52 102 relative to window
  
  (.removeChild layer1 cr) ;use to start over

  ;result: cr disappears immediately, even though window is not active win.


  ; experiment 2, 110211
  (def cr (PClip.))
  (def tx (PText. "Woo! This text is much longer and will go outside box"))
  (.setPathToRectangle cr 50 100 270 150)
  (.addChild cr tx)
  (.addChild layer1 cr)

;result: ditto above, except delay is about 6 seconds; weird
  
  (.translate tx 52 102)

;result: nothing happens until window is selected, then tx appears after
;about *10* seconds--yikes!; but tx is in the "wrong" place, at 52 102
;relative to cr, not relative to window

  ;experiment 3, 110211
  (def wt2 (wrap t2 12 52 116 245))
  (.addChild cr wt2)

  ;result: ditto exp. 1 (wt2 = wrapped text #2)

  (.removeChild cr wt2) ;use to start over

;result: text disappears immediately, even though win. is not active!

;result: tx is in a "default* proportional font, while wt2 appears in the
;"Monospaced" font specified in the definition of 'wrap' fcn. Font chosen
;as the default seems to be the Java logical "Sans-serif" font.

  ;more interactive code
  (def card (clip-box 50 100 270 150 bt t2))
  (.addChild layer1 card)
  (def card2 (clip-box 100 150 270 150 bt t2))
  (.addChild layer1 card2)

  (.removeChild layer1 card)
  (.removeChild layer1 card2)
  )
