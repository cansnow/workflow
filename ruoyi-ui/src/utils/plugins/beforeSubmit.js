export default {
  run(v) {
    console.log(v);
    var updateObj = v.updateObj;
    v.updateObj = updateObj.filter((item, index) => {
      var orgItem = this.queryItemByCondition(window.serverData[item.tid][0].valueList, item.conditions);
      if (!orgItem) {
        return false;
      }
      var result = this.compare(orgItem, item.fields)
      console.log(orgItem,item, result);
      if (result.length > 0) {
        return true;
      }
      return false;
    })
    console.log(v);
    return {
      result: v.updateObj.length != 0,
      data: v
    }
  },
  compare(orgItem, newItem) {
    var result = [];
    for (var i = 0; i < newItem.length; i++) {
      var item = newItem[i];
      console.log(orgItem[item.fieldName] , item.fieldValue);
      if (orgItem[item.fieldName] != item.fieldValue) {
        console.log(orgItem, newItem);
        result.push(item);
      }
    }
    return result;
  },
  queryItemByCondition(orgdata, conditions) {
    for (var i = 0; i < orgdata.length; i++) {
      var orgItem = orgdata[i];
      var isThis = true;
      for (var index = 0; index < conditions.length; index++) {
        var condition = conditions[index];
        var fieldName = condition.fieldName;
        var fieldValue = condition.fieldValue;
        if (orgItem[fieldName] != fieldValue) {
          isThis = false;
          continue;
        }
      }
      if (isThis) {
        return orgItem;
      }
    }
  }
}
