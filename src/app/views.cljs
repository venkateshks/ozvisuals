(ns app.views
  (:require [oz.core :as oz])

  )

(defn group-data [& names]
  (apply concat (for [n names]
                  (map-indexed (fn [i y] {:x i :y y :col n}) (take 20
                                                                   (repeatedly #(rand-int 100)))))))
(group-data "monkey" "slipper" "broom")

(def line-plot
  {:data {:values (group-data "monkey" "slipper" "broom" "dragon")}
   :encoding {:x {:field "x"}
              :y {:field "y"}
              :color {:field "col" :type "nominal"}}
   :mark "line"})


(def tree-plot
  {
   :width 600
   :height 1600
   :padding 5

   :signals [
    {
     :name "labels" :value true
     :bind {:input "checkbox"}
    }
    {
     :name "layout" :value "tidy"
     :bind {:input "radio" :options ["tidy" "cluster"]}
    }
    {
      :name "links" :value "diagonal"
      :bind {
        :input "select"
        :options ["line" "curve" "diagonal" "orthogonal"]
      }
    }
    {
      :name "separation" :value false
      :bind {:input "checkbox"}
    }
  ]

  :data [
    {
      :name "tree"
      :url "flare.json"
      :transform [
        {
          :type "stratify"
          :key "id"
          :parentKey "parent"
        }
        {
          :type "tree"
          :method {:signal "layout"}
          :size [{:signal "height"} {:signal "width - 100"}]
          :separation {:signal "separation"}
          :as ["y" "x" "depth" "children"]
        }
      ]
    }
    {
      :name "links"
      :source "tree"
      :transform [
        { :type "treelinks" }
        {
          :type "linkpath"
          :orient "horizontal"
          :shape {:signal "links"}
        }
      ]
    }
  ]

  :scales [
    {
     :name "color"
     :type "linear"
     :range {:scheme "magma"}
     :domain {:data "tree" :field "depth"}
     :zero true
    }
  ]

  :marks [
    {
     :type "path"
     :from {:data "links"}
     :encode {
              :update {
                       :path {:field "path"}
                       :stroke {:value "#ccc"}
                       }
              }
     }
    {
     :type "symbol"
     :from {:data "tree"}
     :encode {
              :enter {
                      :size {:value 100}
                      :stroke {:value "#fff"}
                      }
              :update {
                       :x {:field "x"}
                       :y {:field "y"}
                       :fill {:scale "color" :field "depth"}
                       }
              }
     }
          {
           :type "text"
           :from {:data "tree"}
           :encode {
                    :enter {
                            :text {:field "name"}
                            :fontSize {:value 9}
                            :baseline {:value "middle"}
                            }
                    :update {
                             :x {:field "x"}
                             :y {:field "y"}
                             :dx {:signal "datum.children ? -7 : 7"}
                             :align {:signal "datum.children ? 'right' : 'left'"}
                             :opacity {:signal "labels ? 1 : 0"}
                             }
                    }
           }
          ]
   }
  )


(defn header
  []
  [:div
   [:h1 "A template for contact tracing"]])


(defn app []
  [:div
   [header]
   ;; [oz.core/vega-lite line-plot]
   [oz.core/vega tree-plot]
   ])
