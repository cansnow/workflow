module.exports = {
    env: {
        browser: true,
        es6: true
    },
    extends: ['plugin:vue/essential'],
    globals: {
        Atomics: 'readonly',
        SharedArrayBuffer: 'readonly'
    },
    parserOptions: {
        parser: 'babel-eslint'
    },
    plugins: ['vue'],
    rules: {
        indent: [0, 4],
        'space-before-function-paren': 0,
		'vue/multi-word-component-names':"off",
        // allow async-await
        'generator-star-spacing': 'off',
		"vue/no-unused-components": "off",
        // allow debugger during development
        'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
        'vue/no-parsing-error': [
            2,
            {
                'x-invalid-end-tag': false
            }
        ],
        'no-undef': 'off',
        camelcase: 'off'
    }
}
