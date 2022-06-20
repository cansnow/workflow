export default [
    {
        title:"未命名表单",
        data:{
            // 行的信息
            rows: [
                { hpx: 100, h: false, s: 's1' },
                null,
                null,
                null,
                null,
                null,
                null,
                { hpx: 56 },
                null,
                null,
                { hpx: 153 },
                { hpx: 91 },
                null,
                { hpx: 145 },
                { hpx: 85 },
                { hpx: 128 }
            ],
            // 列的信息
            columns: [{ wpx: 100, h: false, s: 's1' }, { wpx: 254 }, { wpx: 461 }],

            // 合并单元格的信息
            merges: [
                {
                    start: {
                        rowIndex: 0,
                        columnIndex: 0
                    },
                    end: {
                        rowIndex: 4,
                        columnIndex: 1
                    }
                }
            ],
            // 单元格信息
            cells: {
                '0': [
                    {
                        v: 123,
                        s: 's2'
                    },
                    null,
                    {
                        s: 's2',
                        fs: 'A1',
                        f: '=RC[-2]',
                        v: 123
                    },
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ],
                '7': [
                    null,
                    {
                        v: '提交',
                        c: 'button',
                        s: 's4'
                    },
                    {
                        v: 'cansnow',
                        c: 'text',
                        s: 's4'
                    },
                    null
                ],
                '10': [
                    null,
                    {
                        v: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',
                        c: 'image',
                        s: 's5'
                    },
                    {
                        v: '选项2,选项1',
                        c: 'checkbox',
                        options: [
                            {
                                value: '选项1',
                                label: '黄金糕'
                            },
                            {
                                value: '选项2',
                                label: '双皮奶'
                            },
                            {
                                value: '选项3',
                                label: '蚵仔煎'
                            },
                            {
                                value: '选项4',
                                label: '龙须面'
                            },
                            {
                                value: '选项5',
                                label: '北京烤鸭'
                            }
                        ],
                        s: 's4'
                    },
                    {
                        v: '选项4',
                        c: 'radio',
                        options: [
                            {
                                value: '选项1',
                                label: '黄金糕'
                            },
                            {
                                value: '选项2',
                                label: '双皮奶'
                            },
                            {
                                value: '选项3',
                                label: '蚵仔煎'
                            },
                            {
                                value: '选项4',
                                label: '龙须面'
                            },
                            {
                                value: '选项5',
                                label: '北京烤鸭'
                            }
                        ],
                        s: 's4'
                    }
                ],
                '13': [
                    null,
                    null,
                    {
                        v: '661223',
                        c: 'password',
                        s: 's4'
                    },
                    null,
                    null
                ],
                '14': [
                    null,
                    {
                        v: null,
                        c: 'datetime',
                        s: 's4'
                    },
                    {
                        v: 2611,
                        c: 'number',
                        s: 's4'
                    },
                    null,
                    null,
                    null
                ],
                '15': [
                    null,
                    null,
                    {
                        v: '选项2',
                        c: 'select',
                        options: [
                            {
                                value: '选项1',
                                label: '黄金糕'
                            },
                            {
                                value: '选项2',
                                label: '双皮奶'
                            },
                            {
                                value: '选项3',
                                label: '蚵仔煎'
                            },
                            {
                                value: '选项4',
                                label: '龙须面'
                            },
                            {
                                value: '选项5',
                                label: '北京烤鸭'
                            }
                        ],
                        s: 's4'
                    },
                    {
                        v: [],
                        c: 'selectMultiple',
                        options: [
                            {
                                value: '选项1',
                                label: '黄金糕'
                            },
                            {
                                value: '选项2',
                                label: '双皮奶'
                            },
                            {
                                value: '选项3',
                                label: '蚵仔煎'
                            },
                            {
                                value: '选项4',
                                label: '龙须面'
                            },
                            {
                                value: '选项5',
                                label: '北京烤鸭'
                            }
                        ],
                        s: 's4'
                    },,
                    {
                        v: '',
                        c: 'treeSelect',
                        options: [
                            {
                                id: 1,
                                label: '一级 1',
                                children: [{
                                  id: 4,
                                  label: '二级 1-1',
                                  children: [{
                                    id: 9,
                                    label: '三级 1-1-1'
                                  }, {
                                    id: 10,
                                    label: '三级 1-1-2'
                                  }]
                                }]
                              }, {
                                id: 2,
                                label: '一级 2',
                                children: [{
                                  id: 5,
                                  label: '二级 2-1'
                                }, {
                                  id: 6,
                                  label: '二级 2-2'
                                }]
                              }, {
                                id: 3,
                                label: '一级 3',
                                children: [{
                                  id: 7,
                                  label: '二级 3-1'
                                }, {
                                  id: 8,
                                  label: '二级 3-2'
                                }]
                              }
                        ],
                        s: 's4'
                    },
                    {
                        v: [],
                        c: 'treeSelectMultiple',
                        options: [
                            {
                                id: 1,
                                label: '一级 1',
                                children: [{
                                  id: 4,
                                  label: '二级 1-1',
                                  children: [{
                                    id: 9,
                                    label: '三级 1-1-1'
                                  }, {
                                    id: 10,
                                    label: '三级 1-1-2'
                                  }]
                                }]
                              }, {
                                id: 2,
                                label: '一级 2',
                                children: [{
                                  id: 5,
                                  label: '二级 2-1'
                                }, {
                                  id: 6,
                                  label: '二级 2-2'
                                }]
                              }, {
                                id: 3,
                                label: '一级 3',
                                children: [{
                                  id: 7,
                                  label: '二级 3-1'
                                }, {
                                  id: 8,
                                  label: '二级 3-2'
                                }]
                              }
                        ],
                        s: 's4'
                    },
                    null
                ],
                '16': [
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    {
                        v: null,
                        s: 's4'
                    },
                    {
                        v: null,
                        s: 's4'
                    }
                ],
                '17': [
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    {
                        v: null,
                        s: 's4'
                    },
                    {
                        v: null,
                        s: 's4'
                    }
                ],
                '18': [null, null, null, null, null, null, null, null, null, null, null, null, null],
                '19': [null, null, null, null, null, null, null, null, null, null, null, null, null],
                '20': [null, null, null, null, null, null, null, null, null, null, null, null, null],
                '21': [null, null, null, null, null, null, null, null, null, null, null, null, null]
            },
            // 样式信息
            styles: {
                s2: {
                    border: 'blrt',
                    backgroundColor: '#ffff00',
                    color: '#ff1400',
                    fontFamily: '微软雅黑',
                    fontSize: 12,
                    fontStyle: 'italic',
                    fontWeight: 'bold',
                    textDecoration: 'underline',
                    textAlign: 'center',
                    verticalAlign: 'middle',
                    whiteSpace: 'normal'
                },
                s3: {
                    border: 'blrt',
                    backgroundColor: '#ccc',
                    color: '#ff1400',
                    fontFamily: '微软雅黑',
                    fontSize: 12,
                    fontStyle: 'italic',
                    fontWeight: 'bold',
                    textDecoration: 'underline',
                    textAlign: 'center',
                    verticalAlign: 'middle',
                    whiteSpace: 'normal'
                }
            },
            //行列样式
            RCStyles: {
                s1: {
                    border: undefined,
                    backgroundColor: undefined,
                    color: '#ff1400',
                    fontFamily: '微软雅黑',
                    fontSize: 12,
                    fontStyle: 'italic',
                    fontWeight: 'bold',
                    textDecoration: 'underline',
                    textAlign: 'center',
                    verticalAlign: 'middle',
                    whiteSpace: 'normal'
                }
            },
            //行的数量
            rowCount: 200,
            //列的数量
            columnCount: 20,
            //允许的最大行
            maxRowCount: 10000,
            //允许的最大列
            maxColumnCount: 200
        }
    }
]