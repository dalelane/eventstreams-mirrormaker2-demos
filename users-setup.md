

## 1. create an htpasswd file

```sh
touch htpasswd_file
```

## 2. add users to the htpasswd file

```sh
for i in {1..30}
do
  if [ $i -lt 10 ]; then
    echo -n "password0$i" | htpasswd -i htpasswd_file student0$i
  else
    echo -n "password$i" | htpasswd -i htpasswd_file student$i
  fi
done
```

## 3. add htpasswd file to openshift

```sh
oc create secret generic labs-htpass-secret --from-file=htpasswd=htpasswd_file -n openshift-config
```

## 4. add htpasswd file to cluster identity providers

`oc edit OAuth cluster`

```yaml
spec:
  identityProviders:
    ...
    - htpasswd:
        fileData:
          name: labs-htpass-secret
      mappingMethod: claim
      name: student_users
      type: HTPasswd
```

## 5. create student users

```sh
for i in $(seq -f "%02g" 1 30)
do
  oc create user student$i
  oc create identity student_users:student$i
  oc create useridentitymapping student_users:student$i student$i
done
```


## 6. define role to let students create their own namespaces with their username

```sh
for i in $(seq -f "%02g" 1 30)
do

cat <<EOF | oc apply -f -

apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: student$i-cluster-role
rules:
- apiGroups: ["project.openshift.io"]
  resources: ["projectrequests"]
  verbs: ["create"]
- apiGroups: [""]
  resources: ["namespaces"]
  verbs: ["get", "list"]
- apiGroups: [""]
  resources: ["namespaces"]
  verbs: ["create", "update", "patch", "delete"]
  resourceNames: ["student$i*"]

---

apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: student$i-cluster-role-binding
subjects:
- kind: User
  name: student$i
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: ClusterRole
  name: student$i-cluster-role
  apiGroup: rbac.authorization.k8s.io

EOF
done
```

## 7. pre-create expected namespaces and make the students admins for them

```yaml
allspaces=("apps" "north-america" "south-america" "europe")

for i in $(seq -f "%02g" 1 30)
do
echo "student$i"

for space in "${allspaces[@]}"
do
echo "namespace student$i-$space"

oc new-project student$i-$space

cat <<EOF | oc apply -f -

apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: namespace-admin
  namespace: student$i-$space
rules:
- apiGroups: [""]
  resources: ["*"]
  verbs: ["*"]
- apiGroups: ["apps"]
  resources: ["*"]
  verbs: ["*"]
- apiGroups: ["extensions"]
  resources: ["*"]
  verbs: ["*"]
- apiGroups: ["operators.coreos.com"]
  resources: ["*"]
  verbs: ["*"]
- apiGroups: ["eventstreams.ibm.com"]
  resources: ["*"]
  verbs: ["*"]

---

apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: namespace-admin-binding
  namespace: student$i-$space
subjects:
- kind: User
  name: student$i
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: namespace-admin
  apiGroup: rbac.authorization.k8s.io

EOF

done

done
```
