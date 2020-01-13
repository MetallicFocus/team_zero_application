Vue.component('single-user-info', {
	props: ['username','email','isloggedin'],
	data: function () {
	    return {}
	},
	template:   '<div @click="$emit(\'chat\')">' +
                    '<el-row>' +
                        '<el-col :span="8"><label>{{username}}</label></el-col>' +
                        '<el-col :span="8"><label>{{email}}</label></el-col>' +
                        '<el-col :span="8" v-if="isloggedin"><label>on line</label></el-col>' +
                        '<el-col :span="8" v-else><label>off line</label></el-col>' +
                    '</el-row>' +
                '</div>',
}),

Vue.component('single-chat', {
    props: ['id', 'avatar', 'name', 'time', 'content'],
    data: function () {
        return {}
    },
    template:   '<div class="chat" @click="$emit(\'click-id\')">' +
                    '<el-row>' +
                        '<el-col :span="6"><el-avatar class="avatar" :src="avatar"></el-avatar></el-col>' +
                        '<el-col :span="18">' +
                            '<el-row style="margin-top: 3px;">' +
                                '<el-col :span="18"><label class="name">{{name}}</label></el-col>' +
                                '<el-col :span="6"><label class="last-chat-time">{{time}}</label></el-col>' +
                            '</el-row>' +
                            '<el-row>' +
                                '<el-col :span="24"><label class="last-chat-content">{{content}}</label></el-col>' +
                            '</el-row>' +
                        '</el-col>' +
                    '</el-row>' +
                '</div>',
}),

Vue.component('single-chat-panel', {
    props: ['chat'],
    data: function () {
        return {
            textfield: "",
        }
    },
    template:   '<el-container class="right-panel">' +
                '<el-header id="object-info">' +
                    '<el-avatar class="avatar" :src="chat.avatar" style="margin-top: 5px;"></el-avatar>' +
                    '<label style="font-size: 30px">{{chat.name}}</label>' +
                '</el-header>' +
                '<el-main id="chatting-panel">' +
                    '<div v-for="message in chat.messages">' +
                        '<single-message-from-object v-if="message.objectflag" :avatar="message.avatar" :time="message.time" :content="message.content"></single-message-from-object>' +
                        '<single-message-from-self v-else :avatar="message.avatar" :time="message.time" :content="message.content"></single-message-from-self>' +
                    '</div>' +
                '</el-main>' +
                '<el-footer id="text-panel">' +
                    '<el-row>' +
                        '<el-col :span="22"><el-input v-model="textfield" style="margin-left: 5px;"></el-input></el-col>' +
                        '<el-col :span="1"><el-button type="primary" icon="el-icon-check" @click="send" style="margin-left: 15px;"></el-button></el-col>' +
                    '</el-row>' +
                '</el-footer>' +
                '</el-container>',
    methods: {
        send : function() {
            temp = this.textfield;
            this.textfield = '';
            this.$emit('send-message', [this.chat.name, temp]);
        }
    }
}),

Vue.component('single-message-from-object', {
    props: ['avatar', 'time', 'content'],
    data: function () {
        return {}
    },
    template:   '<div class="message-from-object">' +
                    '<el-row style="margin-top: 20px;">' +
                        '<el-col :span="1" style="margin-right: 10px; margin-top: 2px;"><el-avatar :src="avatar"></el-avatar></el-col>' +
                        '<el-col :span="10"><el-card shadow="always"><label>{{content}}</label><label style="font-size: 12px; float: right; margin-top: 10px;">{{time}}</label></el-card></el-col>' +
                    '</el-row>' +
                '</div>',
}),

Vue.component('single-message-from-self', {
    props: ['avatar', 'time', 'content'],
    data: function () {
        return {}
    },
    template:   '<div class="message-from-self">' +
                    '<el-row style="margin-top: 20px;">' +
                        '<el-col :span="10" :offset="12"><el-card shadow="always"><label style="width: 50px;">{{content}}</label><label style="font-size: 12px; float: right; margin-top: 10px;">{{time}}</label></el-card></el-col>' +
                        '<el-col :span="1" style="margin-left: 10px; margin-top: 2px;"><el-avatar :src="avatar"></el-avatar></el-col>' +
                    '</el-row>' +
                '</div>'
})