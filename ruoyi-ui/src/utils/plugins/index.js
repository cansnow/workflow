import beforeSubmitFn from "./beforeSubmit"
export default {
  beforeSubmit(v){
    return beforeSubmitFn.run(v)
  }
}
