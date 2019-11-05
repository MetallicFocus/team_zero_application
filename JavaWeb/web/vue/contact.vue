<template>
	<div>
		<button>
			<img v-bind:src="portrait"></img>
		</button>
	</div>
</template>

<script>
	module.exports = {
		data : function () {
			return {
				portrait : "../img/Joker.jpg",
				alias : "xiaoming",
				last_contact_date: "Sunday",
				last_message: "Hello!"
			}
		},
	}
</script>

<style>
</style>
