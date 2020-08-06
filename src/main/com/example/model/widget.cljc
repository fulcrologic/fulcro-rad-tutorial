(ns com.example.model.widget
  (:require
    #?(:clj [datomic.api :as d])
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.database-adapters.datomic :as datomic]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.rad.report-options :as ro]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.authorization :as auth]
    [taoensso.timbre :as log]))

(defattr id :widget/id :uuid
  {ao/identity? true
   ao/schema    :production})

(defattr label :widget/label :string
  {ao/schema     :production
   ro/column-heading "MY LABEL"
   fo/validation-message "You must supply a label"
   ao/required? true
   ao/identities #{:widget/id}})

(defattr weight :widget/weight :decimal
  {ao/schema     :production
   ao/identities #{:widget/id}})

(defattr all-widgets :widget/all-widgets :ref
  {::pc/output  [{:widget/all-widgets [:widget/id]}]
   ::pc/resolve (fn [env _]
                  #?(:clj
                     (if-let [db (some-> (get-in env [::datomic/databases :production]) deref)]
                       (let [ids (d/q '[:find [?id ...]
                                        :where
                                        [?e :widget/id ?id]] db)]
                         {:widget/all-widgets (mapv (fn [id] {:widget/id id}) ids)})
                       (log/error "No database atom for production schema!"))))})

(def attributes [id label weight all-widgets])