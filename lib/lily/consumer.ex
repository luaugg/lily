defmodule Lily.Consumer do
  use Nostrum.Consumer

  import Lily.Commands, only: [fetch_command: 1]

  def start_link, do: Consumer.start_link(__MODULE__)

  def handle_event({:MESSAGE_CREATE, msg, _ws_state}) when msg.author.bot != true do
    if String.starts_with?(msg.content, "lily!") do
      content = String.slice(msg.content, 5..-1)
      [head | tail] = String.split(content)

      case fetch_command(head) do
        nil -> :ignore
        command ->
          spawn(fn -> command.(msg, tail) end)
          :ok
      end
    end
  end

  def handle_event(_event), do: :noop
end