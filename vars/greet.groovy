def call(String name){
  echo "hello ${name.replaceAll('[^a-zA-Z0-9\\s-]', '')}"
}
