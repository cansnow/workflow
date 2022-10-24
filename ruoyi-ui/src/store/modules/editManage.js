import { setTemplateId, removeTemplateId } from '@/utils/auth';

const editManage = {
  state: {
    updateTemplate: '',
  },
  mutations: {
    // 修改模板id
		SET_UPDATE_TEMPLATE(state, id) {
      if (id == '') {
        removeTemplateId();
      } else {
        setTemplateId(id);
      }
      state.updateTemplate = id;
		},
  },
  actions: {
    setTemplateId({ commit }, id) {
      commit('SET_UPDATE_TEMPLATE', id);
    }
  }
};

export default editManage;
