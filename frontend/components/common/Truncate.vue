<template>
    <template v-if="(text !== null && text !== undefined) && text.length < stop">
      {{ text }}
    </template>
    <template v-else-if="text !== null && text !== undefined">
      <span
        @mouseenter="show"
        @mouseleave="hide"
        @click="click">
        <template v-if="reverse">
          ...{{ text.substring(text.length - stop) }}
        </template>
        <template v-else>
          {{ text.substring(0, stop) }}...
        </template>

      </span>
      <OverlayPanel
        ref="overflowPanel"
        @hide="hold = false">
        {{text}}
      </OverlayPanel>
    </template>
</template>

<script lang="ts">
import OverlayPanel from 'primevue/overlaypanel';

export default {
	name: "Truncate",
 	props: {
    reverse: {
      type: Boolean,
      default: false
    },
		stop: {
			type: Number,
			default: 20
		},
		text: {
			type: String,
			default: ""
		}
	},
  components: {
    OverlayPanel: OverlayPanel
  },
  data() {
    return {
      hold: false
    }
  },
  methods: {
    click(event) {
      this.hold = !this.hold;
    },
    show(event) {
      this.$refs.overflowPanel.show(event);
    },
    hide(event) {
      if (!this.hold) {
        this.$refs.overflowPanel.hide(event);
      }
    }
  }
}
</script>