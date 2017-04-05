(ns api-test.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [jsoup.soup :as Jsoup]
            [clj-time.coerce :as c]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [clj-http.client :as client]
            [compojure.route :as route]))
(import '[org.jsoup Jsoup])

(def root-url "http://discourse.anahoret.net/")

(def slack-webhook-url "https://hooks.slack.com/services/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")

(defn post-to-slack [request]
  (let [topic_slug (:topic_slug request)
        topic_id (:topic_id request)
        created_at (:created_at request)
        post_number (:post_number request)
        username (:username request)
        name (:name request)
        avatar_template (:avatar_template request)
        cooked (:cooked request)]
    (log/info (str created_at))
    (client/post slack-webhook-url
     {:form-params
      ;{:text (clojure.string/capitalize (clojure.string/replace (str topic_slug) "-" " "))
      {:attachments [{:text (.text (Jsoup/parse cooked))
                      :color "#ba282e"
                      :title_link (str root-url "t/" topic_slug "/" topic_id "/" post_number)
                      :title (str root-url "t/" topic_slug "/" topic_id "/" post_number)
                      :author_name (str name " @"username)
                      :ts (/ (c/to-long (f/parse (f/formatters :date-time) created_at)) 1000)
                      :author_icon (str root-url  (clojure.string/replace avatar_template "{size}" "150"))
                      :footer "forum.anadea.info"
                      :unfurl_links true
                      :image_url (str root-url (get (re-find #"src=\"([^\"]+)" cooked) 1))
                      :author_link (str root-url "users/" username "/summary")}]}
      :content-type :json})))

(defroutes app-routes
  (POST "/" request
    (-> request :body :post (select-keys [:name :post_number :username :created_at :avatar_template :topic_slug :topic_id :cooked]) post-to-slack)
    (let [{:keys [body]} request]
      {:status 200}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
